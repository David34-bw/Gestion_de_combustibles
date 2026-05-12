package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.domain.Venta;
import co.edu.unipiloto.fuelcontrol.dto.request.VentaRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.VentaResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.repository.EstacionRepository;
import co.edu.unipiloto.fuelcontrol.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.unipiloto.fuelcontrol.repository.VehiculoRepository;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final EstacionRepository estacionRepository;
    private static final double CAPACIDAD_MAX = 500.0;
    private static final double UMBRAL_ALERTA = CAPACIDAD_MAX * 0.25; 
    private final VehiculoRepository vehiculoRepository;
    private final PuntosService puntosService;

    public VentaService(VentaRepository ventaRepository,
                    EstacionRepository estacionRepository,
                    VehiculoRepository vehiculoRepository,
                    PuntosService puntosService) {
    this.ventaRepository    = ventaRepository;
    this.estacionRepository = estacionRepository;
    this.vehiculoRepository = vehiculoRepository;
    this.puntosService = puntosService;
}

    @Transactional
public VentaResponse registrar(Long usuarioId, VentaRequest request) {
    Estacion estacion = estacionRepository.findByAdministradorId(usuarioId)
            .orElseThrow(() -> new BadRequestException(
                    "No tienes una estación asociada a tu cuenta"));

    String tipo = request.getTipoCombustible().toUpperCase();
    if (!tipo.equals("GASOLINA") && !tipo.equals("DIESEL")) {
        throw new BadRequestException("Tipo de combustible inválido. Use GASOLINA o DIESEL");
    }

    if (tipo.equals("GASOLINA") && estacion.getStockGasolina() < request.getCantidad()) {
        throw new BadRequestException("Stock de gasolina insuficiente. Disponible: "
                + estacion.getStockGasolina() + " galones");
    }
    if (tipo.equals("DIESEL") && estacion.getStockDiesel() < request.getCantidad()) {
        throw new BadRequestException("Stock de diesel insuficiente. Disponible: "
                + estacion.getStockDiesel() + " galones");
    }

    Usuario comprador = null;
    String placaFinal = null;
    if (request.getPlacaVehiculo() != null && !request.getPlacaVehiculo().isEmpty()) {
        String placa = request.getPlacaVehiculo().toUpperCase().trim();
        placaFinal = placa;
        comprador = vehiculoRepository.findByPlaca(placa)
                .map(vehiculo -> vehiculo.getUsuario())
                .orElseThrow(() -> new BadRequestException(
                        "No se encontró ningún usuario con la placa: " + placa));
    }

    if (tipo.equals("GASOLINA")) {
        estacion.setStockGasolina(estacion.getStockGasolina() - request.getCantidad());
    } else {
        estacion.setStockDiesel(estacion.getStockDiesel() - request.getCantidad());
    }
    estacionRepository.save(estacion);

    boolean alertaGas    = estacion.getCapacidadGasolina() != null
            && estacion.getCapacidadGasolina() > 0
            && estacion.getStockGasolina() < (estacion.getCapacidadGasolina() * 0.25);
    boolean alertaDiesel = estacion.getCapacidadDiesel() != null
            && estacion.getCapacidadDiesel() > 0
            && estacion.getStockDiesel() < (estacion.getCapacidadDiesel() * 0.25);

    Venta venta = Venta.builder()
            .tipoCombustible(tipo)
            .cantidad(request.getCantidad())
            .observaciones(request.getObservaciones())
            .estacion(estacion)
            .usuario(comprador)
            .build();

    Venta guardada = ventaRepository.save(venta);
    puntosService.acumularPuntosPorCompra(comprador, request.getCantidad());
    return toResponse(guardada, alertaGas || alertaDiesel);
}

    public List<VentaResponse> listarPorEstacion(Long usuarioId) {
        Estacion estacion = estacionRepository.findByAdministradorId(usuarioId)
                .orElseThrow(() -> new BadRequestException(
                        "No tienes una estación asociada a tu cuenta"));
        return ventaRepository.findByEstacionIdOrderByFechaVentaDesc(estacion.getId())
                .stream().map(v -> toResponse(v, false)).toList();
    }

    public List<VentaResponse> listarPorUsuario(Long usuarioId) {
    return ventaRepository.findByUsuarioIdOrderByFechaVentaDesc(usuarioId)
            .stream().map(v -> toResponse(v, false)).toList();
}

    private VentaResponse toResponse(Venta v, boolean alerta) {
    // Buscar placa del vehículo del comprador
    String placa = null;
    if (v.getUsuario() != null) {
        placa = vehiculoRepository.findByUsuarioId(v.getUsuario().getId())
                .stream()
                .findFirst()
                .map(veh -> veh.getPlaca())
                .orElse(null);
    }

    return VentaResponse.builder()
            .id(v.getId())
            .tipoCombustible(v.getTipoCombustible())
            .cantidad(v.getCantidad())
            .fechaVenta(v.getFechaVenta())
            .observaciones(v.getObservaciones())
            .estacionId(v.getEstacion().getId())
            .estacionNombre(v.getEstacion().getNombre())
            .usuarioId(v.getUsuario() != null ? v.getUsuario().getId() : null)
            .usuarioNombre(v.getUsuario() != null ? v.getUsuario().getNombre() : "Anónimo")
            .placaVehiculo(placa)
            .alertaStockBajo(alerta)
            .build();
}
}
