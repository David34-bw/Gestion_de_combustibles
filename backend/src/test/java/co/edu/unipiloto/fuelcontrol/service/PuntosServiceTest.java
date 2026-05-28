package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.*;
import co.edu.unipiloto.fuelcontrol.dto.response.CanjeResponse;
import co.edu.unipiloto.fuelcontrol.dto.response.PuntosResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.*;
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
class PuntosServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RecompensaRepository recompensaRepository;
    @Mock private CanjeRecompensaRepository canjeRepository;
    @Mock private UsuarioParticularRepository usuarioParticularRepository;

    @InjectMocks private PuntosService puntosService;

    private Usuario usuario() {
        Usuario u = new Usuario();
        ReflectionTestUtils.setField(u, "id", 1L);
        return u;
    }

    @Test
    void obtenerPuntos_success() {
        UsuarioParticular particular = new UsuarioParticular();
        particular.setPuntosAcumulados(100);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(usuarioParticularRepository.findByUsuarioId(1L)).thenReturn(Optional.of(particular));

        PuntosResponse response = puntosService.obtenerPuntos(1L);

        assertEquals(100, response.getPuntosAcumulados());
    }

    @Test
    void obtenerPuntos_usuarioNotFound_throws() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> puntosService.obtenerPuntos(1L));
    }

    @Test
    void canjear_success() {
        Recompensa recompensa = new Recompensa();
        recompensa.setId(1L);
        recompensa.setCostoPuntos(50);
        recompensa.setActivo(true);
        recompensa.setNombre("Descuento");
        UsuarioParticular particular = new UsuarioParticular();
        particular.setPuntosAcumulados(100);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(recompensaRepository.findById(1L)).thenReturn(Optional.of(recompensa));
        when(usuarioParticularRepository.findByUsuarioId(1L)).thenReturn(Optional.of(particular));
        when(canjeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CanjeResponse response = puntosService.canjear(1L, 1L);

        assertNotNull(response);
        assertEquals(50, particular.getPuntosAcumulados());
    }

    @Test
    void canjear_puntosInsuficientes_throws() {
        Recompensa recompensa = new Recompensa();
        recompensa.setId(1L);
        recompensa.setCostoPuntos(200);
        recompensa.setActivo(true);
        UsuarioParticular particular = new UsuarioParticular();
        particular.setPuntosAcumulados(100);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(recompensaRepository.findById(1L)).thenReturn(Optional.of(recompensa));
        when(usuarioParticularRepository.findByUsuarioId(1L)).thenReturn(Optional.of(particular));

        assertThrows(BadRequestException.class, () -> puntosService.canjear(1L, 1L));
    }

    @Test
    void acumularPuntosPorCompra_conUsuario_success() {
        UsuarioParticular particular = new UsuarioParticular();
        particular.setPuntosAcumulados(0);

        when(usuarioParticularRepository.findByUsuarioId(1L)).thenReturn(Optional.of(particular));

        puntosService.acumularPuntosPorCompra(usuario(), 100.0);

        assertEquals(10, particular.getPuntosAcumulados());
    }

    @Test
    void acumularPuntosPorCompra_usuarioNull_doesNothing() {
        puntosService.acumularPuntosPorCompra(null, 100.0);
        verifyNoInteractions(usuarioParticularRepository);
    }

    @Test
    void acumularPuntosPorCompra_cantidadSmall_doesNothing() {
        puntosService.acumularPuntosPorCompra(usuario(), 5.0);
        verifyNoInteractions(usuarioParticularRepository);
    }

    @Test
    void listarCanjes_success() {
        puntosService.listarCanjes(1L);
        verify(canjeRepository).findByUsuarioIdOrderByFechaCanjeDesc(1L);
    }
}
