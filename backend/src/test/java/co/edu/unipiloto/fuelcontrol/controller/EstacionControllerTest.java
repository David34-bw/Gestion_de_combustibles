package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.request.EstacionCreateRequest;
import co.edu.unipiloto.fuelcontrol.dto.request.EstacionUpdateRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.service.EstacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstacionControllerTest {

    @Mock private EstacionService estacionService;
    @InjectMocks private EstacionController controller;

    private Estacion estacion;

    @BeforeEach
    void setUp() {
        estacion = new Estacion();
        estacion.setId(1L);
        estacion.setNombre("Test");
    }

    @Test
    void listarActivas() {
        when(estacionService.listarActivas()).thenReturn(List.of(estacion));
        ResponseEntity<ApiResponse<List<Estacion>>> res = controller.listarActivas();
        assertEquals(200, res.getStatusCodeValue());
        assertFalse(res.getBody().getData().isEmpty());
    }

    @Test
    void listarTodas() {
        when(estacionService.listarTodas()).thenReturn(List.of(estacion));
        ResponseEntity<ApiResponse<List<Estacion>>> res = controller.listarTodas();
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void buscarPorId() {
        when(estacionService.buscarPorId(1L)).thenReturn(estacion);
        ResponseEntity<ApiResponse<Estacion>> res = controller.buscarPorId(1L);
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void crear() {
        EstacionCreateRequest req = new EstacionCreateRequest();
        req.setNombre("Nueva");
        req.setNit("123");
        when(estacionService.crear(any(), any())).thenReturn(estacion);

        ResponseEntity<ApiResponse<Estacion>> res = controller.crear(req, null);
        assertEquals(201, res.getStatusCodeValue());
    }

    @Test
    void actualizar() {
        EstacionUpdateRequest req = new EstacionUpdateRequest();
        req.setNombre("Actualizada");
        when(estacionService.actualizar(eq(1L), any())).thenReturn(estacion);

        ResponseEntity<ApiResponse<Estacion>> res = controller.actualizar(1L, req);
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void actualizarMiStock() {
        Usuario user = Usuario.builder().build();
        ReflectionTestUtils.setField(user, "id", 1L);
        when(estacionService.buscarPorAdministrador(1L)).thenReturn(estacion);
        when(estacionService.actualizarStock(eq(1L), any(), any())).thenReturn(estacion);

        Map<String, Double> stock = Map.of("gasolina", 50.0, "diesel", 30.0);
        ResponseEntity<ApiResponse<Estacion>> res = controller.actualizarMiStock(user, stock);
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void desactivar() {
        doNothing().when(estacionService).desactivar(1L);
        ResponseEntity<ApiResponse<Void>> res = controller.desactivar(1L);
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void miEstacion() {
        Usuario user = Usuario.builder().build();
        ReflectionTestUtils.setField(user, "id", 1L);
        when(estacionService.buscarPorAdministrador(1L)).thenReturn(estacion);

        ResponseEntity<ApiResponse<Estacion>> res = controller.miEstacion(user);
        assertEquals(200, res.getStatusCodeValue());
    }
}
