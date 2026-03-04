package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.*;
import co.edu.unipiloto.fuelcontrol.domain.enums.EstadoSolicitud;
import co.edu.unipiloto.fuelcontrol.dto.request.ResolucionRequest;
import co.edu.unipiloto.fuelcontrol.dto.request.SolicitudRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.SolicitudResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudCombustibleService {

    private final SolicitudCombustibleRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstacionRepository estacionRepository;
    private final DistribuidorRepository distribuidorRepository;

    // ─── Crear solicitud (cualquier usuario) ─────────────────
    @Transactional
    public SolicitudResponse crearSolicitud(Long usuarioId, SolicitudRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));

        String tipo = request.getTipoCombustible().toUpperCase();
        if (!tipo.equals("GASOLINA") && !tipo.equals("DIESEL")) {
            throw new BadRequestException("Tipo de combustible inválido. Use GASOLINA o DIESEL");
        }

        SolicitudCombustible solicitud = SolicitudCombustible.builder()
                .usuario(usuario)
                .tipoCombustible(tipo)
                .cantidad(request.getCantidad())
                .observaciones(request.getObservaciones())
                .estado(EstadoSolicitud.PENDIENTE)
                .build();

        if (request.getEstacionId() != null) {
            Estacion estacion = estacionRepository.findById(request.getEstacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estación", request.getEstacionId()));
            solicitud.setEstacion(estacion);
        }

        return toResponse(solicitudRepository.save(solicitud));
    }

    // ─── Resolver solicitud (solo REGULADOR) ─────────────────
    @Transactional
    public SolicitudResponse resolverSolicitud(Long solicitudId, Long reguladorId,
                                               ResolucionRequest request) {
        SolicitudCombustible solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud", solicitudId));

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new BadRequestException("La solicitud ya fue resuelta");
        }

        Usuario regulador = usuarioRepository.findById(reguladorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", reguladorId));

        solicitud.setEstado(request.getEstado());
        solicitud.setAprobadoPor(regulador);
        solicitud.setFechaResolucion(LocalDateTime.now());

        if (request.getEstado() == EstadoSolicitud.RECHAZADA) {
            solicitud.setMotivoRechazo(request.getMotivoRechazo());
        }

        if (request.getEstado() == EstadoSolicitud.APROBADA) {
            // Asignar distribuidor si se especificó
            if (request.getDistribuidorId() != null) {
                Distribuidor dist = distribuidorRepository.findById(request.getDistribuidorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Distribuidor", request.getDistribuidorId()));
                solicitud.setDistribuidor(dist);
            }
            // Asignar estación si se especificó
            if (request.getEstacionId() != null) {
                Estacion estacion = estacionRepository.findById(request.getEstacionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Estación", request.getEstacionId()));
                solicitud.setEstacion(estacion);
            }
        }

        return toResponse(solicitudRepository.save(solicitud));
    }

    // ─── Marcar como entregada (DISTRIBUIDOR o ESTACION) ─────
    @Transactional
    public SolicitudResponse marcarEntregada(Long solicitudId) {
        SolicitudCombustible solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud", solicitudId));

        if (solicitud.getEstado() != EstadoSolicitud.APROBADA) {
            throw new BadRequestException("Solo se pueden marcar como entregadas solicitudes aprobadas");
        }
        solicitud.setEstado(EstadoSolicitud.ENTREGADA);
        return toResponse(solicitudRepository.save(solicitud));
    }

    // ─── Consultas ────────────────────────────────────────────
    public List<SolicitudResponse> listarPorUsuario(Long usuarioId) {
        return solicitudRepository.findByUsuarioIdOrderByFechaSolicitudDesc(usuarioId)
                .stream().map(this::toResponse).toList();
    }

    public List<SolicitudResponse> listarTodas() {
        return solicitudRepository.findAllByOrderByFechaSolicitudDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<SolicitudResponse> listarPendientes() {
        return solicitudRepository.findByEstado(EstadoSolicitud.PENDIENTE)
                .stream().map(this::toResponse).toList();
    }

    public List<SolicitudResponse> listarPorEstacion(Long estacionId) {
        return solicitudRepository.findByEstacionId(estacionId)
                .stream().map(this::toResponse).toList();
    }

    public List<SolicitudResponse> listarPorDistribuidor(Long distribuidorId) {
        return solicitudRepository.findByDistribuidorId(distribuidorId)
                .stream().map(this::toResponse).toList();
    }

    public SolicitudResponse buscarPorId(Long id) {
        return toResponse(solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud", id)));
    }

    // ─── Mapper ───────────────────────────────────────────────
    private SolicitudResponse toResponse(SolicitudCombustible s) {
        return SolicitudResponse.builder()
                .id(s.getId())
                .tipoCombustible(s.getTipoCombustible())
                .cantidad(s.getCantidad())
                .estado(s.getEstado())
                .observaciones(s.getObservaciones())
                .motivoRechazo(s.getMotivoRechazo())
                .fechaSolicitud(s.getFechaSolicitud())
                .fechaResolucion(s.getFechaResolucion())
                .usuarioId(s.getUsuario() != null ? s.getUsuario().getId() : null)
                .usuarioNombre(s.getUsuario() != null
                        ? s.getUsuario().getNombre() + " " + s.getUsuario().getApellido() : null)
                .estacionId(s.getEstacion() != null ? s.getEstacion().getId() : null)
                .estacionNombre(s.getEstacion() != null ? s.getEstacion().getNombre() : null)
                .distribuidorId(s.getDistribuidor() != null ? s.getDistribuidor().getId() : null)
                .distribuidorNombre(s.getDistribuidor() != null ? s.getDistribuidor().getNombre() : null)
                .aprobadoPorNombre(s.getAprobadoPor() != null
                        ? s.getAprobadoPor().getNombre() + " " + s.getAprobadoPor().getApellido() : null)
                .build();
    }
}
