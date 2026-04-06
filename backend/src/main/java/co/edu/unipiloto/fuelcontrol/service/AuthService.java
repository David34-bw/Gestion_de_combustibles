package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.config.JwtUtil;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import co.edu.unipiloto.fuelcontrol.dto.request.LoginRequest;
import co.edu.unipiloto.fuelcontrol.dto.request.RegisterRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.AuthResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unipiloto.fuelcontrol.domain.Distribuidor;
import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import co.edu.unipiloto.fuelcontrol.repository.DistribuidorRepository;
import co.edu.unipiloto.fuelcontrol.repository.EstacionRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EstacionRepository estacionRepository;      // ← nuevo
    private final DistribuidorRepository distribuidorRepository; // ← nuevo

    public AuthService(UsuarioRepository usuarioRepository,
                    PasswordEncoder passwordEncoder,
                    JwtUtil jwtUtil,
                    AuthenticationManager authenticationManager,
                    EstacionRepository estacionRepository,
                    DistribuidorRepository distribuidorRepository) {
        this.usuarioRepository      = usuarioRepository;
        this.passwordEncoder        = passwordEncoder;
        this.jwtUtil                = jwtUtil;
        this.authenticationManager  = authenticationManager;
        this.estacionRepository     = estacionRepository;
        this.distribuidorRepository = distribuidorRepository;
    }

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

        Usuario usuario = Usuario.builder()
            .nombre(nombre)
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .numeroDocumento(request.getNumeroDocumento())
            .rol(request.getRol())
            .activo(true)
            .build();

        usuarioRepository.save(usuario);
        if (request.getRol() == Rol.ESTACION) {
            Estacion estacion = new Estacion();
            estacion.setNombre(nombre);
            estacion.setNit(request.getNumeroDocumento());
            estacion.setDireccion("Por definir");
            estacion.setActiva(true);
            estacion.setAdministrador(usuario);
            estacionRepository.save(estacion);
        }

        if (request.getRol() == Rol.DISTRIBUIDOR) {
            Distribuidor distribuidor = new Distribuidor();
            distribuidor.setNombre(nombre);
            distribuidor.setNit(request.getNumeroDocumento() != null
                    ? request.getNumeroDocumento() : request.getEmail());
            distribuidor.setActivo(true);
            distribuidor.setRepresentante(usuario);
            distribuidorRepository.save(distribuidor);
        }
        return buildResponse(jwtUtil.generateToken(usuario), usuario);
    }

    private AuthResponse buildResponse(String token, Usuario usuario) {
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }

    
}