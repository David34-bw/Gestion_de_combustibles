package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.HistorialPrecios;
import co.edu.unipiloto.fuelcontrol.repository.HistorialPreciosRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PrecioService {

    private final HistorialPreciosRepository historialRepository;
    private final Map<String, Map<String, Double>> preciosActuales = new ConcurrentHashMap<>();

    private static final Map<String, Double> DESCUENTO_SUBSIDIO = new HashMap<>();
    static {
        DESCUENTO_SUBSIDIO.put("PARTICULAR",  0.0);
        DESCUENTO_SUBSIDIO.put("TAXI",        8.0);
        DESCUENTO_SUBSIDIO.put("MOTOCICLETA", 5.0);
        DESCUENTO_SUBSIDIO.put("CARGA",      10.0);
    }

    public PrecioService(HistorialPreciosRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @PostConstruct
    public void init() {
        preciosActuales.put("CENTRO",       new ConcurrentHashMap<>(Map.of("GASOLINA", 16491.0, "ACPM", 11276.0)));
        preciosActuales.put("ANTIOQUIA",    new ConcurrentHashMap<>(Map.of("GASOLINA", 16412.0, "ACPM", 11301.0)));
        preciosActuales.put("PACIFICA",     new ConcurrentHashMap<>(Map.of("GASOLINA", 16502.0, "ACPM", 11424.0)));
        preciosActuales.put("CARIBE",       new ConcurrentHashMap<>(Map.of("GASOLINA", 16126.0, "ACPM", 10951.0)));
        preciosActuales.put("EJE_CAFETERO", new ConcurrentHashMap<>(Map.of("GASOLINA", 16439.0, "ACPM", 11363.0)));
        preciosActuales.put("ORINOQUIA",    new ConcurrentHashMap<>(Map.of("GASOLINA", 16591.0, "ACPM", 11376.0)));
        preciosActuales.put("SANTANDERES",  new ConcurrentHashMap<>(Map.of("GASOLINA", 16248.0, "ACPM", 11025.0)));
        preciosActuales.put("SUR_ANDINA",   new ConcurrentHashMap<>(Map.of("GASOLINA", 14247.0, "ACPM", 10338.0)));
        preciosActuales.put("FRONTERA",     new ConcurrentHashMap<>(Map.of("GASOLINA", 14400.0, "ACPM",  9032.0)));

        List<HistorialPrecios> historialDesc = historialRepository.findAllByOrderByFechaCambioDesc();
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

    public double obtenerPrecioFinal(String zona, String tipoCombustible, String tipoVehiculo) {
        String zonaKey = normalizarZona(zona);
        String combustibleKey = tipoCombustible.toUpperCase();
        double precioBase = obtenerPrecioBase(zonaKey, combustibleKey);
        double descuento = DESCUENTO_SUBSIDIO.getOrDefault(tipoVehiculo.toUpperCase(), 0.0);
        double precioFinal = precioBase * (1 - descuento / 100);
        return Math.round(precioFinal);
    }

    public double obtenerPrecioBase(String zona, String tipoCombustible) {
        String zonaKey = normalizarZona(zona);
        String combustibleKey = tipoCombustible.toUpperCase();
        return preciosActuales.getOrDefault(zonaKey, Map.of())
                .getOrDefault(combustibleKey, 0.0);
    }

    public void actualizarPrecio(String zona, String tipoCombustible, Double precio) {
        String zonaKey = normalizarZona(zona);
        String combustibleKey = tipoCombustible.toUpperCase();
        preciosActuales
                .computeIfAbsent(zonaKey, key -> new ConcurrentHashMap<>())
                .put(combustibleKey, precio);
    }

    private String normalizarZona(String zona) {
        if (zona == null) {
            return "CENTRO";
        }
        return zona.trim().toUpperCase().replace(" ", "_");
    }
}
