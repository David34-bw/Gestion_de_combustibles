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
import co.edu.unipiloto.fuelcontrol.domain.Regulador;
import co.edu.unipiloto.fuelcontrol.domain.UsuarioParticular;
import co.edu.unipiloto.fuelcontrol.repository.DistribuidorRepository;
import co.edu.unipiloto.fuelcontrol.repository.EstacionRepository;
import co.edu.unipiloto.fuelcontrol.repository.ReguladorRepository;
import co.edu.unipiloto.fuelcontrol.repository.UsuarioParticularRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EstacionRepository estacionRepository;      // ← nuevo
    private final DistribuidorRepository distribuidorRepository; // ← nuevo
    private final ReguladorRepository reguladorRepository;
    private final UsuarioParticularRepository usuarioParticularRepository;
    private static final String CODIGO_ADMIN = "555";


    public AuthService(UsuarioRepository usuarioRepository,
                    PasswordEncoder passwordEncoder,
                    JwtUtil jwtUtil,
                    AuthenticationManager authenticationManager,
                    EstacionRepository estacionRepository,
                    DistribuidorRepository distribuidorRepository,
                    ReguladorRepository reguladorRepository,
                    UsuarioParticularRepository usuarioParticularRepository) {
        this.usuarioRepository      = usuarioRepository;
        this.passwordEncoder        = passwordEncoder;
        this.jwtUtil                = jwtUtil;
        this.authenticationManager  = authenticationManager;
        this.estacionRepository     = estacionRepository;
        this.distribuidorRepository = distribuidorRepository;
        this.reguladorRepository    = reguladorRepository;
        this.usuarioParticularRepository = usuarioParticularRepository;
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
        if (Rol.ADMINISTRADOR.name().equals(request.getRol())) {
    if (request.getCodigoAdmin() == null || 
        !CODIGO_ADMIN.equals(request.getCodigoAdmin())) {
        throw new RuntimeException("Código de administrador incorrecto");
        // o usa tu clase de excepción personalizada si tienes una
    }
}
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
        if (request.getRol() == Rol.USUARIO) {
            UsuarioParticular particular = new UsuarioParticular();
            particular.setUsuario(usuario);
            particular.setActivo(true);
            usuarioParticularRepository.save(particular);
        }
        if (request.getRol() == Rol.ESTACION) {
            Estacion estacion = new Estacion();
            estacion.setNombre(nombre);
            estacion.setNit(request.getNumeroDocumento());
            String direccion = request.getDireccion();
            estacion.setDireccion((direccion != null && !direccion.isBlank())
                    ? direccion : "Por definir");
            estacion.setCiudad(request.getCiudad());
            estacion.setDepartamento(request.getDepartamento());
            estacion.setActiva(true);
            estacion.setAdministrador(usuario);
            estacionRepository.save(estacion);
        }

        if (request.getRol() == Rol.DISTRIBUIDOR) {
            Distribuidor distribuidor = new Distribuidor();
            distribuidor.setNombre(nombre);
            distribuidor.setNit(request.getNumeroDocumento() != null
                    ? request.getNumeroDocumento() : request.getEmail());
            distribuidor.setCiudad(request.getCiudad());
            distribuidor.setDepartamento(request.getDepartamento());
            distribuidor.setActivo(true);
            distribuidor.setRepresentante(usuario);
            distribuidorRepository.save(distribuidor);
        }
        if (request.getRol() == Rol.REGULADOR) {
            Regulador regulador = new Regulador();
            regulador.setNit(request.getNumeroDocumento());
            regulador.setCodigoEntidad(request.getCodigoEntidad());
            regulador.setCargo(request.getCargo());
            regulador.setDependencia(request.getDependencia());
            regulador.setActivo(true);
            regulador.setUsuario(usuario);
            reguladorRepository.save(regulador);
        }
        return buildResponse(jwtUtil.generateToken(usuario), usuario);
    }

    private AuthResponse buildResponse(String token, Usuario usuario) {
        Integer puntos = null;
        if (usuario.getRol() == Rol.USUARIO) {
            puntos = usuarioParticularRepository.findByUsuarioId(usuario.getId())
                    .map(UsuarioParticular::getPuntosAcumulados)
                    .orElse(0);
        }
        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .puntosAcumulados(puntos)
                .build();
    }

    
}
