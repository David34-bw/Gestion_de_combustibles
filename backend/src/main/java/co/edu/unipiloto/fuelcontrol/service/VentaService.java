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
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final EstacionRepository estacionRepository;
    private static final double CAPACIDAD_MAX = 500.0;
    private static final double UMBRAL_ALERTA = CAPACIDAD_MAX * 0.25; 
    private final VehiculoRepository vehiculoRepository;
    private final PuntosService puntosService;

    private static final String TIPO_GASOLINA = "GASOLINA";
    private static final String TIPO_DIESEL = "DIESEL";

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
        validarTipo(tipo);
        validarCantidad(request.getCantidad());
        validarStock(estacion, tipo, request.getCantidad());

        Usuario comprador = resolveComprador(request.getPlacaVehiculo());

        descontarStock(estacion, tipo, request.getCantidad());
        estacionRepository.save(estacion);

        Venta venta = Venta.builder()
                .tipoCombustible(tipo)
                .cantidad(request.getCantidad())
                .observaciones(request.getObservaciones())
                .estacion(estacion)
                .usuario(comprador)
                .build();

        Venta guardada = ventaRepository.save(venta);
        if (comprador != null) {
            puntosService.acumularPuntosPorCompra(comprador, request.getCantidad());
        }

        return toResponse(guardada, isStockBajo(estacion));
    }

    private void validarTipo(String tipo) {
        if (!tipo.equals(TIPO_GASOLINA) && !tipo.equals(TIPO_DIESEL)) {
            throw new BadRequestException("Tipo de combustible inválido. Use GASOLINA o DIESEL");
        }
    }

    private void validarCantidad(Double cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }
    }

    private void validarStock(Estacion estacion, String tipo, Double cantidad) {
        if (tipo.equals(TIPO_GASOLINA) && estacion.getStockGasolina() < cantidad) {
            throw new BadRequestException("Stock de gasolina insuficiente. Disponible: "
                    + estacion.getStockGasolina() + " galones");
        }
        if (tipo.equals(TIPO_DIESEL) && estacion.getStockDiesel() < cantidad) {
            throw new BadRequestException("Stock de diesel insuficiente. Disponible: "
                    + estacion.getStockDiesel() + " galones");
        }
    }

    private Usuario resolveComprador(String placaVehiculo) {
        if (placaVehiculo == null || placaVehiculo.isBlank()) {
            return null;
        }
        String placa = placaVehiculo.toUpperCase().trim();
        return vehiculoRepository.findByPlaca(placa)
                .map(co.edu.unipiloto.fuelcontrol.domain.Vehiculo::getUsuario)
                .orElseThrow(() -> new BadRequestException(
                        "No se encontró ningún usuario con la placa: " + placa));
    }

    private void descontarStock(Estacion estacion, String tipo, Double cantidad) {
        if (tipo.equals(TIPO_GASOLINA)) {
            estacion.setStockGasolina(estacion.getStockGasolina() - cantidad);
        } else {
            estacion.setStockDiesel(estacion.getStockDiesel() - cantidad);
        }
    }

    private boolean isStockBajo(Estacion estacion) {
        return estacion.getStockGasolina() < UMBRAL_ALERTA || estacion.getStockDiesel() < UMBRAL_ALERTA;
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
        Optional<String> placa = Optional.ofNullable(v.getUsuario())
                .flatMap(u -> vehiculoRepository.findByUsuarioId(u.getId()).stream().findFirst())
                .map(co.edu.unipiloto.fuelcontrol.domain.Vehiculo::getPlaca);

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
                .placaVehiculo(placa.orElse(null))
                .alertaStockBajo(alerta)
                .build();
    }
}
