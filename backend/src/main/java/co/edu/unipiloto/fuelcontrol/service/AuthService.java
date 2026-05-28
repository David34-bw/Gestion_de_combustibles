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
        validarCodigoAdmin(request);
        validarUnicidadEmail(request.getEmail());
        validarUnicidadDocumento(request.getNumeroDocumento());

        String nombre = resolveNombre(request);
        Usuario usuario = buildUsuario(request, nombre);
        usuarioRepository.save(usuario);

        crearEntidadPorRol(request, usuario, nombre);
        return buildResponse(jwtUtil.generateToken(usuario), usuario);
    }

    private void validarCodigoAdmin(RegisterRequest request) {
        if (request.getRol() != Rol.ADMINISTRADOR) {
            return;
        }
        String codigo = request.getCodigoAdmin();
        if (codigo == null || !CODIGO_ADMIN.equals(codigo)) {
            throw new BadRequestException("Código de administrador incorrecto");
        }
    }

    private void validarUnicidadEmail(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new BadRequestException("El email ya está registrado");
        }
    }

    private void validarUnicidadDocumento(String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            return;
        }
        if (usuarioRepository.existsByNumeroDocumento(numeroDocumento)) {
            throw new BadRequestException("El número de documento ya está registrado");
        }
    }

    private String resolveNombre(RegisterRequest request) {
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            return request.getNombre();
        }
        String email = request.getEmail();
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }

    private Usuario buildUsuario(RegisterRequest request, String nombre) {
        return Usuario.builder()
                .nombre(nombre)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .numeroDocumento(request.getNumeroDocumento())
                .rol(request.getRol())
                .activo(true)
                .build();
    }

    private void crearEntidadPorRol(RegisterRequest request, Usuario usuario, String nombre) {
        Rol rol = request.getRol();
        if (rol == Rol.USUARIO) {
            crearUsuarioParticular(usuario);
            return;
        }
        if (rol == Rol.ESTACION) {
            crearEstacion(request, usuario, nombre);
            return;
        }
        if (rol == Rol.DISTRIBUIDOR) {
            crearDistribuidor(request, usuario, nombre);
            return;
        }
        if (rol == Rol.REGULADOR) {
            crearRegulador(request, usuario);
        }
    }

    private void crearUsuarioParticular(Usuario usuario) {
        UsuarioParticular particular = new UsuarioParticular();
        particular.setUsuario(usuario);
        particular.setActivo(true);
        usuarioParticularRepository.save(particular);
    }

    private void crearEstacion(RegisterRequest request, Usuario usuario, String nombre) {
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

    private void crearDistribuidor(RegisterRequest request, Usuario usuario, String nombre) {
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

    private void crearRegulador(RegisterRequest request, Usuario usuario) {
        Regulador regulador = new Regulador();
        regulador.setNit(request.getNumeroDocumento());
        regulador.setCodigoEntidad(request.getCodigoEntidad());
        regulador.setCargo(request.getCargo());
        regulador.setDependencia(request.getDependencia());
        regulador.setActivo(true);
        regulador.setUsuario(usuario);
        reguladorRepository.save(regulador);
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
