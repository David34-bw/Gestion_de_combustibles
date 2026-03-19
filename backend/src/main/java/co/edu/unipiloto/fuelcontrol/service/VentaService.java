package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import co.edu.unipiloto.fuelcontrol.domain.Venta;
import co.edu.unipiloto.fuelcontrol.dto.request.VentaRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.VentaResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.repository.EstacionRepository;
import co.edu.unipiloto.fuelcontrol.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final EstacionRepository estacionRepository;
    private static final double CAPACIDAD_MAX = 500.0;
    private static final double UMBRAL_ALERTA = CAPACIDAD_MAX * 0.25; 

    public VentaService(VentaRepository ventaRepository,
                        EstacionRepository estacionRepository) {
        this.ventaRepository   = ventaRepository;
        this.estacionRepository = estacionRepository;
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

        // Verificar stock disponible
        if (tipo.equals("GASOLINA") && estacion.getStockGasolina() < request.getCantidad()) {
            throw new BadRequestException("Stock de gasolina insuficiente. Disponible: "
                    + estacion.getStockGasolina() + " galones");
        }
        if (tipo.equals("DIESEL") && estacion.getStockDiesel() < request.getCantidad()) {
            throw new BadRequestException("Stock de diesel insuficiente. Disponible: "
                    + estacion.getStockDiesel() + " galones");
        }

        // Descontar stock
        if (tipo.equals("GASOLINA")) {
            estacion.setStockGasolina(estacion.getStockGasolina() - request.getCantidad());
        } else {
            estacion.setStockDiesel(estacion.getStockDiesel() - request.getCantidad());
        }
        estacionRepository.save(estacion);

        boolean alertaGas    = estacion.getStockGasolina() < UMBRAL_ALERTA;
        boolean alertaDiesel = estacion.getStockDiesel() < UMBRAL_ALERTA;
        boolean hayAlerta    = alertaGas || alertaDiesel;

        // Registrar venta
        Venta venta = Venta.builder()
                .tipoCombustible(tipo)
                .cantidad(request.getCantidad())
                .observaciones(request.getObservaciones())
                .estacion(estacion)
                .build();

        return toResponse(ventaRepository.save(venta), hayAlerta);
    }

    public List<VentaResponse> listarPorEstacion(Long usuarioId) {
        Estacion estacion = estacionRepository.findByAdministradorId(usuarioId)
                .orElseThrow(() -> new BadRequestException(
                        "No tienes una estación asociada a tu cuenta"));
        return ventaRepository.findByEstacionIdOrderByFechaVentaDesc(estacion.getId())
                .stream().map(v -> toResponse(v, false)).toList();
    }

    private VentaResponse toResponse(Venta v, boolean alerta) {
        return VentaResponse.builder()
                .id(v.getId())
                .tipoCombustible(v.getTipoCombustible())
                .cantidad(v.getCantidad())
                .fechaVenta(v.getFechaVenta())
                .observaciones(v.getObservaciones())
                .estacionId(v.getEstacion().getId())
                .estacionNombre(v.getEstacion().getNombre())
                .alertaStockBajo(alerta)
                .build();
    }
}