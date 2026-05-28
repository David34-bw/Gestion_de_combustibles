package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.HistorialPrecios;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.request.PrecioUpdateRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.repository.HistorialPreciosRepository;
import co.edu.unipiloto.fuelcontrol.service.PrecioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/precios")
public class PrecioController {

    private final HistorialPreciosRepository historialRepository;
    private final PrecioService precioService;

    // Usamos ConcurrentHashMap mutable para poder actualizar precios a nivel de instancia
    private final Map<String, Map<String, Double>> preciosActuales = new ConcurrentHashMap<>();

    private static final String COMBUSTIBLE_GASOLINA = "GASOLINA";

    private static final Map<String, Double> DESCUENTO_SUBSIDIO = new HashMap<>();
    static {
        DESCUENTO_SUBSIDIO.put("PARTICULAR",  0.0);
        DESCUENTO_SUBSIDIO.put("TAXI",        8.0);
        DESCUENTO_SUBSIDIO.put("MOTOCICLETA", 5.0);
        DESCUENTO_SUBSIDIO.put("CARGA",      10.0);
    }

    public PrecioController(HistorialPreciosRepository historialRepository,
                            PrecioService precioService) {
        this.historialRepository = historialRepository;
        this.precioService = precioService;
    }

    @PostConstruct
    public void init() {
        // Inicializar con valores base
        preciosActuales.put("CENTRO",       new ConcurrentHashMap<>(Map.of(COMBUSTIBLE_GASOLINA, 16491.0, "ACPM", 11276.0)));
        preciosActuales.put("ANTIOQUIA",    new ConcurrentHashMap<>(Map.of(COMBUSTIBLE_GASOLINA, 16412.0, "ACPM", 11301.0)));
        preciosActuales.put("PACIFICA",     new ConcurrentHashMap<>(Map.of(COMBUSTIBLE_GASOLINA, 16502.0, "ACPM", 11424.0)));
        preciosActuales.put("CARIBE",       new ConcurrentHashMap<>(Map.of(COMBUSTIBLE_GASOLINA, 16126.0, "ACPM", 10951.0)));
        preciosActuales.put("EJE_CAFETERO", new ConcurrentHashMap<>(Map.of(COMBUSTIBLE_GASOLINA, 16439.0, "ACPM", 11363.0)));
        preciosActuales.put("ORINOQUIA",    new ConcurrentHashMap<>(Map.of(COMBUSTIBLE_GASOLINA, 16591.0, "ACPM", 11376.0)));
        preciosActuales.put("SANTANDERES",  new ConcurrentHashMap<>(Map.of(COMBUSTIBLE_GASOLINA, 16248.0, "ACPM", 11025.0)));
        preciosActuales.put("SUR_ANDINA",   new ConcurrentHashMap<>(Map.of(COMBUSTIBLE_GASOLINA, 14247.0, "ACPM", 10338.0)));
        preciosActuales.put("FRONTERA",     new ConcurrentHashMap<>(Map.of(COMBUSTIBLE_GASOLINA, 14400.0, "ACPM",  9032.0)));

        // Cargar últimos precios de la base de datos si existen (sobrescribe valores base)
        List<HistorialPrecios> historialDesc = historialRepository.findAllByOrderByFechaCambioDesc();
        // Iteramos de fin a principio para que los más recientes (al principio de la lista) se guarden de últimos y sobrevivan
        for (int i = historialDesc.size() - 1; i >= 0; i--) {
            HistorialPrecios h = historialDesc.get(i);
            String zona = h.getZona();
            String tipo = h.getTipoCombustible();
            Double precioNuevo = h.getPrecioNuevo();
            if (preciosActuales.containsKey(zona) && preciosActuales.get(zona).containsKey(tipo)) {
                preciosActuales.get(zona).put(tipo, precioNuevo);
            }
        }
    }

    /**
     * GET /api/precios?zona=CENTRO&tipoCombustible=GASOLINA&tipoVehiculo=PARTICULAR
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> consultar(
            @RequestParam String zona,
            @RequestParam String tipoCombustible,
            @RequestParam(defaultValue = "PARTICULAR") String tipoVehiculo) {

        String zonaKey       = zona.toUpperCase().replace(" ", "_");
        String combustibleKey= tipoCombustible.toUpperCase();

        if (!preciosActuales.containsKey(zonaKey)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Zona no válida: " + zona));
        }
        if (!preciosActuales.get(zonaKey).containsKey(combustibleKey)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tipo de combustible no válido"));
        }

        double precioBase  = preciosActuales.get(zonaKey).get(combustibleKey);
        double descuento   = DESCUENTO_SUBSIDIO.getOrDefault(tipoVehiculo.toUpperCase(), 0.0);
        double precioFinal = precioService.obtenerPrecioFinal(zonaKey, combustibleKey, tipoVehiculo);

        // Verificar si hubo cambio reciente
        boolean huboCambio = historialRepository
                .findTopByZonaAndTipoCombustibleOrderByFechaCambioDesc(zonaKey, combustibleKey)
                .isPresent();

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("zona",            zona);
        resultado.put("tipoCombustible", tipoCombustible);
        resultado.put("tipoVehiculo",    tipoVehiculo);
        resultado.put("precioBase",      precioBase);
        resultado.put("descuentoPct",    descuento);
        resultado.put("precioFinal",     precioFinal);
        resultado.put("unidad",          "COP/galón");
        resultado.put("huboCambioReciente", huboCambio);

        return ResponseEntity.ok(ApiResponse.ok("Precio consultado", resultado));
    }

    /**
     * PUT /api/precios - Solo REGULADOR actualiza precios
     */
    @PutMapping
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(
            @AuthenticationPrincipal Usuario regulador,
            @Valid @RequestBody PrecioUpdateRequest request) {

        String zonaKey       = request.getZona().toUpperCase().replace(" ", "_");
        String combustibleKey= request.getTipoCombustible().toUpperCase();

        if (!preciosActuales.containsKey(zonaKey)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Zona no válida"));
        }

        double precioAnterior = preciosActuales.get(zonaKey)
                .getOrDefault(combustibleKey, 0.0);

        // Actualizar precio en memoria
        preciosActuales.get(zonaKey).put(combustibleKey, request.getPrecio());
        precioService.actualizarPrecio(zonaKey, combustibleKey, request.getPrecio());

        // Guardar en historial
        HistorialPrecios historial = HistorialPrecios.builder()
                .zona(zonaKey)
                .tipoCombustible(combustibleKey)
                .precioAnterior(precioAnterior)
                .precioNuevo(request.getPrecio())
                .cambiadoPor(regulador.getNombre())
                .build();
        historialRepository.save(historial);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("zona",            zonaKey);
        respuesta.put("tipoCombustible", combustibleKey);
        respuesta.put("precioAnterior",  precioAnterior);
        respuesta.put("precioNuevo",     request.getPrecio());

        return ResponseEntity.ok(ApiResponse.ok("Precio actualizado", respuesta));
    }

    /**
     * GET /api/precios/historial - Historial de cambios
     */
    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<List<HistorialPrecios>>> historial() {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                historialRepository.findAllByOrderByFechaCambioDesc()));
    }

    /**
     * GET /api/precios/zonas
     */
    @GetMapping("/zonas")
    public ResponseEntity<ApiResponse<Object>> zonas() {
        return ResponseEntity.ok(ApiResponse.ok("OK", preciosActuales.keySet()));
    }
}
