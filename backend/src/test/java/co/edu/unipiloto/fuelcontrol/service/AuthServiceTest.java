package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.config.JwtUtil;
import co.edu.unipiloto.fuelcontrol.domain.*;
import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import co.edu.unipiloto.fuelcontrol.dto.request.LoginRequest;
import co.edu.unipiloto.fuelcontrol.dto.request.RegisterRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.AuthResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private EstacionRepository estacionRepository;
    @Mock private DistribuidorRepository distribuidorRepository;
    @Mock private ReguladorRepository reguladorRepository;
    @Mock private UsuarioParticularRepository usuarioParticularRepository;

    @InjectMocks private AuthService authService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");
        request.setNombre("Test User");
        request.setNumeroDocumento("12345");
    }

    @Test
    void register_whenAdminWithValidCode_success() {
        request.setRol(Rol.ADMINISTRADOR);
        request.setCodigoAdmin("555");
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(usuarioRepository).save(any());
    }

    @Test
    void register_whenAdminWithInvalidCode_throws() {
        request.setRol(Rol.ADMINISTRADOR);
        request.setCodigoAdmin("wrong");
        assertThrows(BadRequestException.class, () -> authService.register(request));
    }

    @Test
    void register_whenEmailAlreadyExists_throws() {
        request.setRol(Rol.USUARIO);
        when(usuarioRepository.existsByEmail("test@test.com")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> authService.register(request));
    }

    @Test
    void register_whenDocumentAlreadyExists_throws() {
        request.setRol(Rol.USUARIO);
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(usuarioRepository.existsByNumeroDocumento("12345")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> authService.register(request));
    }

    @Test
    void register_whenUsuarioRole_createsParticular() {
        request.setRol(Rol.USUARIO);
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(usuarioParticularRepository.findByUsuarioId(any())).thenReturn(Optional.of(new UsuarioParticular()));

        authService.register(request);

        verify(usuarioParticularRepository).save(any(UsuarioParticular.class));
    }

    @Test
    void register_whenEstacionRole_createsEstacion() {
        request.setRol(Rol.ESTACION);
        request.setCiudad("Bogota");
        request.setDepartamento("Cundinamarca");
        request.setDireccion("Calle 123");
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(usuarioParticularRepository.findByUsuarioId(any())).thenReturn(Optional.of(new UsuarioParticular()));

        authService.register(request);

        verify(estacionRepository).save(any(Estacion.class));
    }

    @Test
    void register_whenDistribuidorRole_createsDistribuidor() {
        request.setRol(Rol.DISTRIBUIDOR);
        request.setCiudad("Bogota");
        request.setDepartamento("Cundinamarca");
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(usuarioParticularRepository.findByUsuarioId(any())).thenReturn(Optional.of(new UsuarioParticular()));

        authService.register(request);

        verify(distribuidorRepository).save(any(Distribuidor.class));
    }

    @Test
    void register_whenReguladorRole_createsRegulador() {
        request.setRol(Rol.REGULADOR);
        request.setCodigoEntidad("ENT-001");
        request.setCargo("Inspector");
        request.setDependencia("MinEnergia");
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(usuarioParticularRepository.findByUsuarioId(any())).thenReturn(Optional.of(new UsuarioParticular()));

        authService.register(request);

        verify(reguladorRepository).save(any(Regulador.class));
    }

    @Test
    void register_whenNombreEmpty_usesEmailPrefix() {
        request.setRol(Rol.USUARIO);
        request.setNombre("");
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(usuarioParticularRepository.findByUsuarioId(any())).thenReturn(Optional.of(new UsuarioParticular()));

        authService.register(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("test", captor.getValue().getNombre());
    }

    @Test
    void login_success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("pass");

        Usuario usuario = Usuario.builder().email("test@test.com").rol(Rol.USUARIO).build();
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateToken(usuario)).thenReturn("token");
        when(usuarioParticularRepository.findByUsuarioId(any())).thenReturn(Optional.of(new UsuarioParticular()));

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void register_whenDocumentoNull_doesNotCheckUniqueness() {
        request.setRol(Rol.USUARIO);
        request.setNumeroDocumento(null);
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(usuarioParticularRepository.findByUsuarioId(any())).thenReturn(Optional.of(new UsuarioParticular()));

        assertDoesNotThrow(() -> authService.register(request));
        verify(usuarioRepository, never()).existsByNumeroDocumento(any());
    }
}
