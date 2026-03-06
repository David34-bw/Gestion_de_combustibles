package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/precios")
public class PrecioController {

    // Precios en pesos colombianos por galón (fuente: SICOM 2024)
    // Estructura: zona -> { gasolina, acpm }
    private static final Map<String, Map<String, Double>> PRECIOS = new HashMap<>();

    static {
        PRECIOS.put("CENTRO",        Map.of("GASOLINA", 16491.0, "ACPM", 11276.0));
        PRECIOS.put("ANTIOQUIA",     Map.of("GASOLINA", 16412.0, "ACPM", 11301.0));
        PRECIOS.put("PACIFICA",      Map.of("GASOLINA", 16502.0, "ACPM", 11424.0));
        PRECIOS.put("CARIBE",        Map.of("GASOLINA", 16126.0, "ACPM", 10951.0));
        PRECIOS.put("EJE_CAFETERO",  Map.of("GASOLINA", 16439.0, "ACPM", 11363.0));
        PRECIOS.put("ORINOQUIA",     Map.of("GASOLINA", 16591.0, "ACPM", 11376.0));
        PRECIOS.put("SANTANDERES",   Map.of("GASOLINA", 16248.0, "ACPM", 11025.0));
        PRECIOS.put("SUR_ANDINA",    Map.of("GASOLINA", 14247.0, "ACPM", 10338.0));
        PRECIOS.put("FRONTERA",      Map.of("GASOLINA", 14400.0, "ACPM",  9032.0));
    }

    // Descuento subsidio por tipo de vehículo (%)
    private static final Map<String, Double> DESCUENTO_SUBSIDIO = new HashMap<>();
    static {
        DESCUENTO_SUBSIDIO.put("PARTICULAR",   0.0);
        DESCUENTO_SUBSIDIO.put("TAXI",         8.0);
        DESCUENTO_SUBSIDIO.put("MOTOCICLETA",  5.0);
        DESCUENTO_SUBSIDIO.put("CARGA",       10.0);
    }

    /**
     * GET /api/precios?zona=CENTRO&tipoCombustible=GASOLINA&tipoVehiculo=PARTICULAR
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> consultar(
            @RequestParam String zona,
            @RequestParam String tipoCombustible,
            @RequestParam(defaultValue = "PARTICULAR") String tipoVehiculo) {

        String zonaKey = zona.toUpperCase().replace(" ", "_");
        String combustibleKey = tipoCombustible.toUpperCase();

        if (!PRECIOS.containsKey(zonaKey)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Zona no válida: " + zona));
        }

        Map<String, Double> preciosZona = PRECIOS.get(zonaKey);
        if (!preciosZona.containsKey(combustibleKey)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tipo de combustible no válido: " + tipoCombustible));
        }

        double precioBase       = preciosZona.get(combustibleKey);
        double descuento        = DESCUENTO_SUBSIDIO.getOrDefault(tipoVehiculo.toUpperCase(), 0.0);
        double precioSubsidiado = precioBase * (1 - descuento / 100);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("zona",             zona);
        resultado.put("tipoCombustible",  tipoCombustible);
        resultado.put("tipoVehiculo",     tipoVehiculo);
        resultado.put("precioBase",       precioBase);
        resultado.put("descuentoPct",     descuento);
        resultado.put("precioFinal",      Math.round(precioSubsidiado));
        resultado.put("unidad",           "COP/galón");

        return ResponseEntity.ok(ApiResponse.ok("Precio consultado", resultado));
    }

    /**
     * GET /api/precios/zonas — lista todas las zonas disponibles
     */
    @GetMapping("/zonas")
    public ResponseEntity<ApiResponse<Object>> zonas() {
        return ResponseEntity.ok(ApiResponse.ok("OK", PRECIOS.keySet()));
    }
}
