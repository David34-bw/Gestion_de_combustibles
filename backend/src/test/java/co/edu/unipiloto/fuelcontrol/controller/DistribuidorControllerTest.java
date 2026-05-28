package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Distribuidor;
import co.edu.unipiloto.fuelcontrol.dto.request.DistribuidorCreateRequest;
import co.edu.unipiloto.fuelcontrol.dto.request.DistribuidorUpdateRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.service.DistribuidorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistribuidorControllerTest {

    @Mock private DistribuidorService distribuidorService;
    @InjectMocks private DistribuidorController controller;

    @Test
    void listarActivos() {
        when(distribuidorService.listarActivos()).thenReturn(List.of(new Distribuidor()));
        ResponseEntity<ApiResponse<List<Distribuidor>>> res = controller.listarActivos();
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void buscarPorId() {
        when(distribuidorService.buscarPorId(1L)).thenReturn(new Distribuidor());
        ResponseEntity<ApiResponse<Distribuidor>> res = controller.buscarPorId(1L);
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void crear() {
        DistribuidorCreateRequest req = new DistribuidorCreateRequest();
        req.setNombre("Test");
        req.setNit("NIT123");
        when(distribuidorService.crear(any(), any())).thenReturn(new Distribuidor());

        ResponseEntity<ApiResponse<Distribuidor>> res = controller.crear(req, null);
        assertEquals(201, res.getStatusCodeValue());
    }

    @Test
    void actualizar() {
        DistribuidorUpdateRequest req = new DistribuidorUpdateRequest();
        req.setNombre("Actualizado");
        when(distribuidorService.actualizar(eq(1L), any())).thenReturn(new Distribuidor());

        ResponseEntity<ApiResponse<Distribuidor>> res = controller.actualizar(1L, req);
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void desactivar() {
        ResponseEntity<ApiResponse<Void>> res = controller.desactivar(1L);
        assertEquals(200, res.getStatusCodeValue());
    }
}
