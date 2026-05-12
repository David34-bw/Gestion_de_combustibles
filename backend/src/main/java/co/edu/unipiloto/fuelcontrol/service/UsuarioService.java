package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import co.edu.unipiloto.fuelcontrol.dto.request.UsuarioUpdateDTO;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    // Reemplaza el método actualizar en UsuarioService.java
    public Usuario actualizar(Long id, UsuarioUpdateDTO datos) {
    Usuario usuario = buscarPorId(id);
    
    if (datos.nombre() != null) usuario.setNombre(datos.nombre());
    if (datos.email() != null) usuario.setEmail(datos.email());
    if (datos.numeroDocumento() != null) usuario.setNumeroDocumento(datos.numeroDocumento());
    
    // Solo actualiza la contraseña si no viene vacía
    if (datos.password() != null && !datos.password().isBlank()) {
        usuario.setPassword(passwordEncoder.encode(datos.password()));
    }
    
    return usuarioRepository.save(usuario);
}

    public void desactivar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void activar(Long id) {
        // Usa directamente el repositorio para evitar filtros de lógica de negocio antiguos
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }   

    public void eliminar(Long id) {
    usuarioRepository.deleteById(id);
    }   
}
