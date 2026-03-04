package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.request.ResolucionRequest;
import co.edu.unipiloto.fuelcontrol.dto.request.SolicitudRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.dto.response.SolicitudResponse;
import co.edu.unipiloto.fuelcontrol.service.SolicitudCombustibleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudCombustibleController {

    private final SolicitudCombustibleService solicitudService;

    /**
     * POST /api/solicitudes - Cualquier usuario autenticado
     * Body: { "tipoCombustible": "GASOLINA", "cantidad": 50.0,
     *         "observaciones": "...", "estacionId": 1 }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SolicitudResponse>> crearSolicitud(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody SolicitudRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Solicitud creada",
                        solicitudService.crearSolicitud(usuario.getId(), request)));
    }

    /** GET /api/solicitudes/mis-solicitudes */
    @GetMapping("/mis-solicitudes")
    public ResponseEntity<ApiResponse<List<SolicitudResponse>>> misSolicitudes(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                solicitudService.listarPorUsuario(usuario.getId())));
    }

    /** GET /api/solicitudes - Solo REGULADOR ve todas */
    @GetMapping
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<List<SolicitudResponse>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok("OK", solicitudService.listarTodas()));
    }

    /** GET /api/solicitudes/pendientes - REGULADOR */
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<List<SolicitudResponse>>> listarPendientes() {
        return ResponseEntity.ok(ApiResponse.ok("OK", solicitudService.listarPendientes()));
    }

    /** GET /api/solicitudes/estacion/{id} - ESTACION, REGULADOR */
    @GetMapping("/estacion/{id}")
    @PreAuthorize("hasAnyRole('ESTACION', 'REGULADOR')")
    public ResponseEntity<ApiResponse<List<SolicitudResponse>>> listarPorEstacion(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                solicitudService.listarPorEstacion(id)));
    }

    /** GET /api/solicitudes/distribuidor/{id} - DISTRIBUIDOR, REGULADOR */
    @GetMapping("/distribuidor/{id}")
    @PreAuthorize("hasAnyRole('DISTRIBUIDOR', 'REGULADOR')")
    public ResponseEntity<ApiResponse<List<SolicitudResponse>>> listarPorDistribuidor(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                solicitudService.listarPorDistribuidor(id)));
    }

    /** GET /api/solicitudes/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SolicitudResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK", solicitudService.buscarPorId(id)));
    }

    /**
     * POST /api/solicitudes/{id}/resolver - Solo REGULADOR
     * Body: { "estado": "APROBADA", "distribuidorId": 1, "estacionId": 1 }
     *    o: { "estado": "RECHAZADA", "motivoRechazo": "Sin stock disponible" }
     */
    @PostMapping("/{id}/resolver")
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<SolicitudResponse>> resolverSolicitud(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario regulador,
            @Valid @RequestBody ResolucionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Solicitud resuelta",
                solicitudService.resolverSolicitud(id, regulador.getId(), request)));
    }

    /**
     * PATCH /api/solicitudes/{id}/entregar - DISTRIBUIDOR o ESTACION marca como entregada
     */
    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasAnyRole('DISTRIBUIDOR', 'ESTACION', 'REGULADOR')")
    public ResponseEntity<ApiResponse<SolicitudResponse>> marcarEntregada(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Marcada como entregada",
                solicitudService.marcarEntregada(id)));
    }
}
