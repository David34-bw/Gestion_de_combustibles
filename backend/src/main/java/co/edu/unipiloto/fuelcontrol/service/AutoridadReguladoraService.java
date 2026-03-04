package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.AutoridadReguladora;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.AutoridadReguladoraRepository;
import co.edu.unipiloto.fuelcontrol.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoridadReguladoraService {

    private final AutoridadReguladoraRepository autoridadRepository;
    private final UsuarioRepository usuarioRepository;

    public AutoridadReguladora crear(AutoridadReguladora autoridad, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));

        if (autoridadRepository.findByUsuarioId(usuarioId).isPresent()) {
            throw new BadRequestException("El usuario ya tiene un perfil de autoridad reguladora");
        }

        autoridad.setUsuario(usuario);
        return autoridadRepository.save(autoridad);
    }

    public List<AutoridadReguladora> listarActivas() {
        return autoridadRepository.findByActivaTrue();
    }

    public List<AutoridadReguladora> listarTodas() {
        return autoridadRepository.findAll();
    }

    public AutoridadReguladora buscarPorId(Long id) {
        return autoridadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutoridadReguladora", id));
    }

    public AutoridadReguladora buscarPorUsuarioId(Long usuarioId) {
        return autoridadRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró autoridad reguladora para el usuario " + usuarioId));
    }

    public AutoridadReguladora actualizar(Long id, AutoridadReguladora datos) {
        AutoridadReguladora autoridad = buscarPorId(id);
        if (datos.getNombre() != null)              autoridad.setNombre(datos.getNombre());
        if (datos.getCargo() != null)               autoridad.setCargo(datos.getCargo());
        if (datos.getDependencia() != null)         autoridad.setDependencia(datos.getDependencia());
        if (datos.getCupoGasolinaMensual() != null) autoridad.setCupoGasolinaMensual(datos.getCupoGasolinaMensual());
        if (datos.getCupoDieselMensual() != null)   autoridad.setCupoDieselMensual(datos.getCupoDieselMensual());
        return autoridadRepository.save(autoridad);
    }

    public void desactivar(Long id) {
        AutoridadReguladora autoridad = buscarPorId(id);
        autoridad.setActiva(false);
        autoridadRepository.save(autoridad);
    }
}
