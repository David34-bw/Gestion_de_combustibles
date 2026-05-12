package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.CanjeRecompensa;
import co.edu.unipiloto.fuelcontrol.domain.Recompensa;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.response.CanjeResponse;
import co.edu.unipiloto.fuelcontrol.dto.response.PuntosResponse;
import co.edu.unipiloto.fuelcontrol.dto.response.RecompensaResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.CanjeRecompensaRepository;
import co.edu.unipiloto.fuelcontrol.repository.RecompensaRepository;
import co.edu.unipiloto.fuelcontrol.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PuntosService {

    private final UsuarioRepository usuarioRepository;
    private final RecompensaRepository recompensaRepository;
    private final CanjeRecompensaRepository canjeRepository;

    public PuntosService(UsuarioRepository usuarioRepository,
                         RecompensaRepository recompensaRepository,
                         CanjeRecompensaRepository canjeRepository) {
        this.usuarioRepository = usuarioRepository;
        this.recompensaRepository = recompensaRepository;
        this.canjeRepository = canjeRepository;
    }

    public PuntosResponse obtenerPuntos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return PuntosResponse.builder()
                .usuarioId(usuario.getId())
                .puntosAcumulados(usuario.getPuntosAcumulados())
                .build();
    }

    public List<CanjeResponse> listarCanjes(Long usuarioId) {
        return canjeRepository.findByUsuarioIdOrderByFechaCanjeDesc(usuarioId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public CanjeResponse canjear(Long usuarioId, Long recompensaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Recompensa recompensa = recompensaRepository.findById(recompensaId)
                .orElseThrow(() -> new ResourceNotFoundException("Recompensa no encontrada"));
        if (!Boolean.TRUE.equals(recompensa.getActivo())) {
            throw new BadRequestException("Recompensa no disponible");
        }
        int puntosActuales = usuario.getPuntosAcumulados() != null
                ? usuario.getPuntosAcumulados() : 0;
        if (puntosActuales < recompensa.getCostoPuntos()) {
            throw new BadRequestException("Puntos insuficientes para canjear");
        }
        usuario.setPuntosAcumulados(puntosActuales - recompensa.getCostoPuntos());
        usuarioRepository.save(usuario);

        CanjeRecompensa canje = new CanjeRecompensa();
        canje.setUsuario(usuario);
        canje.setRecompensa(recompensa);
        canjeRepository.save(canje);

        return toResponse(canje);
    }

    @Transactional
    public void acumularPuntosPorCompra(Usuario usuario, double cantidad) {
        if (usuario == null || cantidad <= 0) {
            return;
        }
        int puntos = (int) Math.floor(cantidad / 10.0);
        if (puntos <= 0) {
            return;
        }
        int actuales = usuario.getPuntosAcumulados() != null
                ? usuario.getPuntosAcumulados() : 0;
        usuario.setPuntosAcumulados(actuales + puntos);
        usuarioRepository.save(usuario);
    }

    private CanjeResponse toResponse(CanjeRecompensa canje) {
        Recompensa recompensa = canje.getRecompensa();
        RecompensaResponse recompensaResponse = RecompensaResponse.builder()
                .id(recompensa.getId())
                .nombre(recompensa.getNombre())
                .descripcion(recompensa.getDescripcion())
                .costoPuntos(recompensa.getCostoPuntos())
                .porcentajeDescuento(recompensa.getPorcentajeDescuento())
                .build();
        return CanjeResponse.builder()
                .id(canje.getId())
                .fechaCanje(canje.getFechaCanje())
                .estado(canje.getEstado())
                .puntosConsumidos(recompensa.getCostoPuntos())
                .recompensa(recompensaResponse)
                .build();
    }
}
