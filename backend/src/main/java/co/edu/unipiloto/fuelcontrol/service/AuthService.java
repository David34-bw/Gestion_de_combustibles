package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.config.JwtUtil;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.request.LoginRequest;
import co.edu.unipiloto.fuelcontrol.dto.request.RegisterRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.AuthResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        return buildResponse(jwtUtil.generateToken(usuario), usuario);
    }

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        if (request.getNumeroDocumento() != null && !request.getNumeroDocumento().isEmpty()
                && usuarioRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new BadRequestException("El número de documento ya está registrado");
        }

        // Nombre por defecto si no viene
        String nombre   = (request.getNombre() != null && !request.getNombre().isEmpty())
                ? request.getNombre()
                : request.getEmail().split("@")[0];
        String apellido = (request.getApellido() != null) ? request.getApellido() : "";

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .numeroDocumento(request.getNumeroDocumento())
                .telefono(request.getTelefono())
                .rol(request.getRol())
                .activo(true)
                .placa(request.getPlaca())
                .tipoVehiculo(request.getTipoVehiculo())
                .aplicaSubsidio(request.getAplicaSubsidio() != null ? request.getAplicaSubsidio() : false)
                .numeroRunt(request.getNumeroRunt())
                .build();

        usuarioRepository.save(usuario);
        return buildResponse(jwtUtil.generateToken(usuario), usuario);
    }

    private AuthResponse buildResponse(String token, Usuario usuario) {
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }
}