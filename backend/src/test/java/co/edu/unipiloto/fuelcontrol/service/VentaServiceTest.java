package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.domain.Vehiculo;
import co.edu.unipiloto.fuelcontrol.domain.Venta;
import co.edu.unipiloto.fuelcontrol.dto.request.VentaRequest;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.repository.EstacionRepository;
import co.edu.unipiloto.fuelcontrol.repository.VentaRepository;
import co.edu.unipiloto.fuelcontrol.repository.VehiculoRepository;
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
class VentaServiceTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private EstacionRepository estacionRepository;
    @Mock private VehiculoRepository vehiculoRepository;
    @Mock private PuntosService puntosService;

    @InjectMocks private VentaService ventaService;

    private VentaRequest request;
    private Estacion estacion;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().email("admin@estacion.com").build();
        ReflectionTestUtils.setField(usuario, "id", 1L);
        estacion = new Estacion();
        estacion.setId(1L);
        estacion.setStockGasolina(100.0);
        estacion.setStockDiesel(100.0);
        estacion.setNombre("Estacion Test");

        request = new VentaRequest();
        request.setTipoCombustible("GASOLINA");
        request.setCantidad(10.0);
    }

    @Test
    void registrar_gasolina_success() {
        when(estacionRepository.findByAdministradorId(1L)).thenReturn(Optional.of(estacion));
        when(ventaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ventaService.registrar(1L, request);

        verify(ventaRepository).save(any());
        assertEquals(90.0, estacion.getStockGasolina());
    }

    @Test
    void registrar_diesel_success() {
        request.setTipoCombustible("DIESEL");
        when(estacionRepository.findByAdministradorId(1L)).thenReturn(Optional.of(estacion));
        when(ventaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ventaService.registrar(1L, request);

        assertEquals(90.0, estacion.getStockDiesel());
    }

    @Test
    void registrar_invalidTipoCombustible_throws() {
        request.setTipoCombustible("INVALIDO");
        when(estacionRepository.findByAdministradorId(1L)).thenReturn(Optional.of(estacion));

        assertThrows(BadRequestException.class, () -> ventaService.registrar(1L, request));
    }

    @Test
    void registrar_cantidadNull_throws() {
        request.setCantidad(null);
        when(estacionRepository.findByAdministradorId(1L)).thenReturn(Optional.of(estacion));

        assertThrows(BadRequestException.class, () -> ventaService.registrar(1L, request));
    }

    @Test
    void registrar_cantidadNegative_throws() {
        request.setCantidad(-5.0);
        when(estacionRepository.findByAdministradorId(1L)).thenReturn(Optional.of(estacion));

        assertThrows(BadRequestException.class, () -> ventaService.registrar(1L, request));
    }

    @Test
    void registrar_stockInsuficiente_throws() {
        request.setCantidad(200.0);
        when(estacionRepository.findByAdministradorId(1L)).thenReturn(Optional.of(estacion));

        assertThrows(BadRequestException.class, () -> ventaService.registrar(1L, request));
    }

    @Test
    void registrar_withPlacaVehiculo_success() {
        when(estacionRepository.findByAdministradorId(1L)).thenReturn(Optional.of(estacion));
        when(ventaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        request.setPlacaVehiculo("ABC123");

        Vehiculo vehiculo = Vehiculo.builder().placa("ABC123").usuario(usuario).build();
        when(vehiculoRepository.findByPlaca("ABC123")).thenReturn(Optional.of(vehiculo));

        ventaService.registrar(1L, request);

        verify(puntosService).acumularPuntosPorCompra(usuario, 10.0);
    }

    @Test
    void registrar_estacionNotFound_throws() {
        when(estacionRepository.findByAdministradorId(1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> ventaService.registrar(1L, request));
    }

    @Test
    void listarPorEstacion_success() {
        when(estacionRepository.findByAdministradorId(1L)).thenReturn(Optional.of(estacion));

        ventaService.listarPorEstacion(1L);

        verify(ventaRepository).findByEstacionIdOrderByFechaVentaDesc(1L);
    }

    @Test
    void listarPorUsuario_success() {
        ventaService.listarPorUsuario(1L);

        verify(ventaRepository).findByUsuarioIdOrderByFechaVentaDesc(1L);
    }
}
