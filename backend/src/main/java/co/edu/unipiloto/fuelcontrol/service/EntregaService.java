package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Distribuidor;
import co.edu.unipiloto.fuelcontrol.domain.EntregaCombustible;
import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import co.edu.unipiloto.fuelcontrol.dto.request.EntregaRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.EntregaResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.DistribuidorRepository;
import co.edu.unipiloto.fuelcontrol.repository.EntregaRepository;
import co.edu.unipiloto.fuelcontrol.repository.EstacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final DistribuidorRepository distribuidorRepository;
    private final EstacionRepository estacionRepository;
    private static final double CAPACIDAD_MAX = 500.0;

    public EntregaService(EntregaRepository entregaRepository,
                          DistribuidorRepository distribuidorRepository,
                          EstacionRepository estacionRepository) {
        this.entregaRepository      = entregaRepository;
        this.distribuidorRepository = distribuidorRepository;
        this.estacionRepository     = estacionRepository;
    }

    @Transactional
    public EntregaResponse registrar(Long distribuidorId, EntregaRequest request) {
        Distribuidor distribuidor = distribuidorRepository.findById(distribuidorId)
                .orElseThrow(() -> new ResourceNotFoundException("Distribuidor", distribuidorId));

        Estacion estacion = estacionRepository.findById(request.getEstacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Estación", request.getEstacionId()));

        String tipo = request.getTipoCombustible().toUpperCase();
        if (!tipo.equals("GASOLINA") && !tipo.equals("DIESEL")) {
            throw new BadRequestException("Tipo de combustible inválido. Use GASOLINA o DIESEL");
        }

        // Descontar stock del distribuidor
        if (tipo.equals("GASOLINA")) {
            double nuevaVenta = safe(distribuidor.getVentaGasolina()) + request.getVolumen();
            distribuidor.setVentaGasolina(nuevaVenta);
        } else {
            double nuevaVenta = safe(distribuidor.getVentaDiesel()) + request.getVolumen();
            distribuidor.setVentaDiesel(nuevaVenta);
        }
        distribuidorRepository.save(distribuidor);

        // Sumar stock a la estación
        // Sumar stock a la estación con validación de capacidad máxima
        if (tipo.equals("GASOLINA")) {
            double nuevoStock = estacion.getStockGasolina() + request.getVolumen();
            if (nuevoStock > CAPACIDAD_MAX) {
                throw new BadRequestException(
                    "La estación no puede recibir más gasolina. Capacidad máxima: 500 galones. " +
                    "Stock actual: " + estacion.getStockGasolina() + " galones");
            }
            estacion.setStockGasolina(nuevoStock);
        } else {
            double nuevoStock = estacion.getStockDiesel() + request.getVolumen();
            if (nuevoStock > CAPACIDAD_MAX) {
                throw new BadRequestException(
                    "La estación no puede recibir más diesel. Capacidad máxima: 500 galones. " +
                    "Stock actual: " + estacion.getStockDiesel() + " galones");
            }
            estacion.setStockDiesel(nuevoStock);
        }

        // Registrar entrega
        EntregaCombustible entrega = EntregaCombustible.builder()
                .tipoCombustible(tipo)
                .volumen(request.getVolumen())
                .observaciones(request.getObservaciones())
                .distribuidor(distribuidor)
                .estacion(estacion)
                .build();

        return toResponse(entregaRepository.save(entrega));
    }

    private double safe(Double valor) {
        return valor == null ? 0.0 : valor;
    }

    public List<EntregaResponse> listarPorDistribuidor(Long distribuidorId) {
        return entregaRepository
                .findByDistribuidorIdOrderByFechaEntregaDesc(distribuidorId)
                .stream().map(this::toResponse).toList();
    }

    public List<EntregaResponse> listarPorEstacion(Long estacionId) {
        return entregaRepository
                .findByEstacionIdOrderByFechaEntregaDesc(estacionId)
                .stream().map(this::toResponse).toList();
    }

    private EntregaResponse toResponse(EntregaCombustible e) {
        return EntregaResponse.builder()
                .id(e.getId())
                .tipoCombustible(e.getTipoCombustible())
                .volumen(e.getVolumen())
                .fechaEntrega(e.getFechaEntrega())
                .observaciones(e.getObservaciones())
                .distribuidorId(e.getDistribuidor().getId())
                .distribuidorNombre(e.getDistribuidor().getNombre())
                .estacionId(e.getEstacion().getId())
                .estacionNombre(e.getEstacion().getNombre())
                .build();
    }
        // Busca el distribuidor cuyo representante es el usuario autenticado
        @Transactional
    public EntregaResponse registrarPorUsuario(Long usuarioId, EntregaRequest request) {
        Distribuidor distribuidor = distribuidorRepository.findByRepresentanteId(usuarioId)
                .orElseThrow(() -> new BadRequestException(
                        "No tienes un distribuidor asociado a tu cuenta"));
        return registrar(distribuidor.getId(), request);
    }

    public List<EntregaResponse> listarPorUsuario(Long usuarioId) {
        Distribuidor distribuidor = distribuidorRepository.findByRepresentanteId(usuarioId)
                .orElseThrow(() -> new BadRequestException(
                        "No tienes un distribuidor asociado a tu cuenta"));
        return listarPorDistribuidor(distribuidor.getId());
    }
    }
