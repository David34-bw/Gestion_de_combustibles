package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.HistorialPrecios;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.request.PrecioUpdateRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.repository.HistorialPreciosRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/precios")
public class PrecioController {

    private final HistorialPreciosRepository historialRepository;


    // Precios actuales en memoria — se actualizan dinámicamente
    private static final Map<String, Map<String, Double>> PRECIOS = new HashMap<>();

    static {
        PRECIOS.put("CENTRO",       Map.of("GASOLINA", 16491.0, "ACPM", 11276.0));
        PRECIOS.put("ANTIOQUIA",    Map.of("GASOLINA", 16412.0, "ACPM", 11301.0));
        PRECIOS.put("PACIFICA",     Map.of("GASOLINA", 16502.0, "ACPM", 11424.0));
        PRECIOS.put("CARIBE",       Map.of("GASOLINA", 16126.0, "ACPM", 10951.0));
        PRECIOS.put("EJE_CAFETERO", Map.of("GASOLINA", 16439.0, "ACPM", 11363.0));
        PRECIOS.put("ORINOQUIA",    Map.of("GASOLINA", 16591.0, "ACPM", 11376.0));
        PRECIOS.put("SANTANDERES",  Map.of("GASOLINA", 16248.0, "ACPM", 11025.0));
        PRECIOS.put("SUR_ANDINA",   Map.of("GASOLINA", 14247.0, "ACPM", 10338.0));
        PRECIOS.put("FRONTERA",     Map.of("GASOLINA", 14400.0, "ACPM",  9032.0));
    }

    // Usamos HashMap mutable para poder actualizar precios
    private static final Map<String, Map<String, Double>> PRECIOS_MUTABLES = new HashMap<>();

    static {
        PRECIOS.forEach((zona, precios) ->
                PRECIOS_MUTABLES.put(zona, new HashMap<>(precios)));
    }

    private static final Map<String, Double> DESCUENTO_SUBSIDIO = new HashMap<>();
    static {
        DESCUENTO_SUBSIDIO.put("PARTICULAR",  0.0);
        DESCUENTO_SUBSIDIO.put("TAXI",        8.0);
        DESCUENTO_SUBSIDIO.put("MOTOCICLETA", 5.0);
        DESCUENTO_SUBSIDIO.put("CARGA",      10.0);
    }

    public PrecioController(HistorialPreciosRepository historialRepository) {
        this.historialRepository = historialRepository;
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

        if (!PRECIOS_MUTABLES.containsKey(zonaKey)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Zona no válida: " + zona));
        }
        if (!PRECIOS_MUTABLES.get(zonaKey).containsKey(combustibleKey)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tipo de combustible no válido"));
        }

        double precioBase  = PRECIOS_MUTABLES.get(zonaKey).get(combustibleKey);
        double descuento   = DESCUENTO_SUBSIDIO.getOrDefault(tipoVehiculo.toUpperCase(), 0.0);
        double precioFinal = precioBase * (1 - descuento / 100);

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
        resultado.put("precioFinal",     Math.round(precioFinal));
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

        if (!PRECIOS_MUTABLES.containsKey(zonaKey)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Zona no válida"));
        }

        double precioAnterior = PRECIOS_MUTABLES.get(zonaKey)
                .getOrDefault(combustibleKey, 0.0);

        // Actualizar precio en memoria
        PRECIOS_MUTABLES.get(zonaKey).put(combustibleKey, request.getPrecio());

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
        return ResponseEntity.ok(ApiResponse.ok("OK", PRECIOS_MUTABLES.keySet()));
    }
}