package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.HistorialPrecios;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.request.PrecioUpdateRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.repository.HistorialPreciosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrecioControllerTest {

    @Mock private HistorialPreciosRepository historialRepository;
    @InjectMocks private PrecioController controller;

    @BeforeEach
    void setUp() {
        when(historialRepository.findAllByOrderByFechaCambioDesc()).thenReturn(List.of());
        controller.init();
    }

    @Test
    void consultar_precioGasolina() {
        ResponseEntity<ApiResponse<Map<String, Object>>> res =
                controller.consultar("CENTRO", "GASOLINA", "PARTICULAR");

        assertEquals(200, res.getStatusCodeValue());
        Map<String, Object> data = res.getBody().getData();
        assertEquals("CENTRO", data.get("zona"));
        assertEquals("GASOLINA", data.get("tipoCombustible"));
        assertEquals("COP/galón", data.get("unidad"));
        assertTrue((Double) data.get("precioBase") > 0);
    }

    @Test
    void consultar_precioACPM() {
        ResponseEntity<ApiResponse<Map<String, Object>>> res =
                controller.consultar("CENTRO", "ACPM", "PARTICULAR");

        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void consultar_conDescuentoTaxi() {
        ResponseEntity<ApiResponse<Map<String, Object>>> res =
                controller.consultar("CENTRO", "GASOLINA", "TAXI");

        assertEquals(200, res.getStatusCodeValue());
        Map<String, Object> data = res.getBody().getData();
        assertTrue((Double) data.get("precioFinal") < (Double) data.get("precioBase"));
    }

    @Test
    void consultar_zonaInvalida_returns400() {
        ResponseEntity<ApiResponse<Map<String, Object>>> res =
                controller.consultar("ZONA_INVALIDA", "GASOLINA", "PARTICULAR");

        assertEquals(400, res.getStatusCodeValue());
    }

    @Test
    void consultar_tipoCombustibleInvalido_returns400() {
        ResponseEntity<ApiResponse<Map<String, Object>>> res =
                controller.consultar("CENTRO", "HIDROGENO", "PARTICULAR");

        assertEquals(400, res.getStatusCodeValue());
    }

    @Test
    void actualizar_precio_success() {
        when(historialRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PrecioUpdateRequest req = new PrecioUpdateRequest();
        req.setZona("CENTRO");
        req.setTipoCombustible("GASOLINA");
        req.setPrecio(17000.0);

        Usuario regulador = Usuario.builder().nombre("Regulador Test").build();
        ResponseEntity<ApiResponse<Map<String, Object>>> res =
                controller.actualizar(regulador, req);

        assertEquals(200, res.getStatusCodeValue());
        verify(historialRepository).save(any(HistorialPrecios.class));
    }

    @Test
    void actualizar_zonaInvalida_returns400() {
        PrecioUpdateRequest req = new PrecioUpdateRequest();
        req.setZona("INVALIDA");
        req.setTipoCombustible("GASOLINA");
        req.setPrecio(17000.0);

        Usuario regulador = Usuario.builder().nombre("Regulador Test").build();
        ResponseEntity<ApiResponse<Map<String, Object>>> res =
                controller.actualizar(regulador, req);

        assertEquals(400, res.getStatusCodeValue());
    }

    @Test
    void historial_success() {
        when(historialRepository.findAllByOrderByFechaCambioDesc()).thenReturn(List.of());
        ResponseEntity<ApiResponse<List<HistorialPrecios>>> res = controller.historial();
        assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void zonas_success() {
        ResponseEntity<ApiResponse<Object>> res = controller.zonas();
        assertEquals(200, res.getStatusCodeValue());
    }
}
