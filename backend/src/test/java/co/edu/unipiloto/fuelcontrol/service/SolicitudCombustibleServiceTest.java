package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.SolicitudCombustible;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.domain.enums.EstadoSolicitud;
import co.edu.unipiloto.fuelcontrol.dto.request.SolicitudRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.SolicitudResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudCombustibleServiceTest {

    @Mock private SolicitudCombustibleRepository solicitudRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EstacionRepository estacionRepository;
    @Mock private DistribuidorRepository distribuidorRepository;

    @InjectMocks private SolicitudCombustibleService solicitudService;

    private SolicitudRequest request;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().email("test@test.com").build();
        ReflectionTestUtils.setField(usuario, "id", 1L);

        request = new SolicitudRequest();
        request.setTipoCombustible("GASOLINA");
        request.setCantidad(50.0);
    }

    @Test
    void crearSolicitud_success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(solicitudRepository.save(any())).thenAnswer(i -> {
            SolicitudCombustible s = i.getArgument(0);
            return SolicitudCombustible.builder().estado(EstadoSolicitud.PENDIENTE).build();
        });

        SolicitudResponse response = solicitudService.crearSolicitud(1L, request);

        assertNotNull(response);
    }

    @Test
    void crearSolicitud_usuarioNotFound_throws() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> solicitudService.crearSolicitud(99L, request));
    }

    @Test
    void crearSolicitud_invalidTipo_throws() {
        request.setTipoCombustible("OTRO");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(BadRequestException.class, () -> solicitudService.crearSolicitud(1L, request));
    }

    @Test
    void buscarPorId_success() {
        SolicitudCombustible solicitud = SolicitudCombustible.builder()
                .estado(EstadoSolicitud.PENDIENTE).build();
        ReflectionTestUtils.setField(solicitud, "id", 1L);
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        SolicitudResponse response = solicitudService.buscarPorId(1L);

        assertNotNull(response);
    }

    @Test
    void buscarPorId_notFound_throws() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> solicitudService.buscarPorId(99L));
    }

    @Test
    void listarTodas_success() {
        solicitudService.listarTodas();
        verify(solicitudRepository).findAllByOrderByFechaSolicitudDesc();
    }

    @Test
    void listarPendientes_success() {
        solicitudService.listarPendientes();
        verify(solicitudRepository).findByEstado(EstadoSolicitud.PENDIENTE);
    }

    @Test
    void listarPorUsuario_success() {
        solicitudService.listarPorUsuario(1L);
        verify(solicitudRepository).findByUsuarioIdOrderByFechaSolicitudDesc(1L);
    }

    @Test
    void listarPorEstacion_success() {
        solicitudService.listarPorEstacion(1L);
        verify(solicitudRepository).findByEstacionId(1L);
    }

    @Test
    void listarPorDistribuidor_success() {
        solicitudService.listarPorDistribuidor(1L);
        verify(solicitudRepository).findByDistribuidorId(1L);
    }
}
