package co.edu.unipiloto.fuelcontrol.service;

import co.edu.unipiloto.fuelcontrol.domain.Distribuidor;
import co.edu.unipiloto.fuelcontrol.domain.EntregaCombustible;
import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import co.edu.unipiloto.fuelcontrol.dto.request.EntregaRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.EntregaResponse;
import co.edu.unipiloto.fuelcontrol.exception.BadRequestException;
import co.edu.unipiloto.fuelcontrol.exception.ResourceNotFoundException;
import co.edu.unipiloto.fuelcontrol.repository.DistribuidorRepository;
import co.edu.unipiloto.fuelcontrol.repository.EntregaRepository;
import co.edu.unipiloto.fuelcontrol.repository.EstacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntregaServiceTest {

    @Mock private EntregaRepository entregaRepository;
    @Mock private DistribuidorRepository distribuidorRepository;
    @Mock private EstacionRepository estacionRepository;

    @InjectMocks private EntregaService entregaService;

    private EntregaRequest request;
    private Distribuidor distribuidor;
    private Estacion estacion;

    @BeforeEach
    void setUp() {
        distribuidor = new Distribuidor();
        distribuidor.setId(1L);
        distribuidor.setNombre("Distribuidor Test");
        distribuidor.setVentaGasolina(0.0);
        distribuidor.setVentaDiesel(0.0);

        estacion = new Estacion();
        estacion.setId(1L);
        estacion.setNombre("Estacion Test");
        estacion.setStockGasolina(0.0);
        estacion.setStockDiesel(0.0);

        request = new EntregaRequest();
        request.setTipoCombustible("GASOLINA");
        request.setVolumen(50.0);
        request.setEstacionId(1L);
    }

    @Test
    void registrar_gasolina_success() {
        when(distribuidorRepository.findById(1L)).thenReturn(Optional.of(distribuidor));
        when(estacionRepository.findById(1L)).thenReturn(Optional.of(estacion));
        when(entregaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        EntregaResponse response = entregaService.registrar(1L, request);

        assertNotNull(response);
        assertEquals(50.0, distribuidor.getVentaGasolina());
        assertEquals(50.0, estacion.getStockGasolina());
    }

    @Test
    void registrar_diesel_success() {
        request.setTipoCombustible("DIESEL");
        when(distribuidorRepository.findById(1L)).thenReturn(Optional.of(distribuidor));
        when(estacionRepository.findById(1L)).thenReturn(Optional.of(estacion));
        when(entregaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        entregaService.registrar(1L, request);

        assertEquals(50.0, distribuidor.getVentaDiesel());
        assertEquals(50.0, estacion.getStockDiesel());
    }

    @Test
    void registrar_invalidTipo_throws() {
        request.setTipoCombustible("OTRO");
        when(distribuidorRepository.findById(1L)).thenReturn(Optional.of(distribuidor));
        when(estacionRepository.findById(1L)).thenReturn(Optional.of(estacion));

        assertThrows(BadRequestException.class, () -> entregaService.registrar(1L, request));
    }

    @Test
    void registrar_distribuidorNotFound_throws() {
        when(distribuidorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> entregaService.registrar(99L, request));
    }

    @Test
    void registrar_estacionNotFound_throws() {
        request.setEstacionId(99L);
        when(distribuidorRepository.findById(1L)).thenReturn(Optional.of(distribuidor));
        when(estacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> entregaService.registrar(1L, request));
    }

    @Test
    void registrar_capacidadExcedida_throws() {
        request.setVolumen(600.0);
        when(distribuidorRepository.findById(1L)).thenReturn(Optional.of(distribuidor));
        when(estacionRepository.findById(1L)).thenReturn(Optional.of(estacion));

        assertThrows(BadRequestException.class, () -> entregaService.registrar(1L, request));
    }

    @Test
    void registrarPorUsuario_success() {
        when(distribuidorRepository.findByRepresentanteId(1L)).thenReturn(Optional.of(distribuidor));
        when(estacionRepository.findById(1L)).thenReturn(Optional.of(estacion));
        when(entregaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        EntregaResponse response = entregaService.registrarPorUsuario(1L, request);

        assertNotNull(response);
    }

    @Test
    void registrarPorUsuario_noDistribuidor_throws() {
        when(distribuidorRepository.findByRepresentanteId(1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> entregaService.registrarPorUsuario(1L, request));
    }

    @Test
    void listarPorDistribuidor_success() {
        entregaService.listarPorDistribuidor(1L);
        verify(entregaRepository).findByDistribuidorIdOrderByFechaEntregaDesc(1L);
    }

    @Test
    void listarPorEstacion_success() {
        entregaService.listarPorEstacion(1L);
        verify(entregaRepository).findByEstacionIdOrderByFechaEntregaDesc(1L);
    }

    @Test
    void listarPorUsuario_success() {
        when(distribuidorRepository.findByRepresentanteId(1L)).thenReturn(Optional.of(distribuidor));

        entregaService.listarPorUsuario(1L);

        verify(entregaRepository).findByDistribuidorIdOrderByFechaEntregaDesc(1L);
    }
}
