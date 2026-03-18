package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Estacion;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.service.EstacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import co.edu.unipiloto.fuelcontrol.domain.Usuario;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estaciones")
public class EstacionController {

    private final EstacionService estacionService;
    
    public EstacionController(EstacionService estacionService) {
        this.estacionService = estacionService;
    }

    /** GET /api/estaciones/publicas - Sin autenticación (para mostrar en app) */
    @GetMapping("/publicas")
    public ResponseEntity<ApiResponse<List<Estacion>>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.ok("OK", estacionService.listarActivas()));
    }

    /** GET /api/estaciones - REGULADOR */
    @GetMapping
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<List<Estacion>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok("OK", estacionService.listarTodas()));
    }

    /** GET /api/estaciones/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Estacion>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK", estacionService.buscarPorId(id)));
    }

    /** POST /api/estaciones - REGULADOR crea estación */
    @PostMapping
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<Estacion>> crear(
            @RequestBody Estacion estacion,
            @RequestParam(required = false) Long administradorId) {
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Estación creada", estacionService.crear(estacion, administradorId)));
    }

    /** PUT /api/estaciones/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('REGULADOR', 'ESTACION')")
    public ResponseEntity<ApiResponse<Estacion>> actualizar(
            @PathVariable Long id,
            @RequestBody Estacion datos) {
        return ResponseEntity.ok(ApiResponse.ok("Estación actualizada",
                estacionService.actualizar(id, datos)));
    }

    /**
     * PATCH /api/estaciones/mi-estacion/stock
     * La estación actualiza su propio stock
     */
    @PatchMapping("/mi-estacion/stock")
    @PreAuthorize("hasRole('ESTACION')")
    public ResponseEntity<ApiResponse<Estacion>> actualizarMiStock(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody Map<String, Double> stock) {
        Estacion estacion = estacionService.buscarPorAdministrador(usuario.getId());
        return ResponseEntity.ok(ApiResponse.ok("Stock actualizado",
                estacionService.actualizarStock(estacion.getId(),
                        stock.get("gasolina"), stock.get("diesel"))));
    }

    /** DELETE /api/estaciones/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        estacionService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Estación desactivada", null));
    }


    /**
     * GET /api/estaciones/mi-estacion
     * La estación ve su propio inventario
     */
    @GetMapping("/mi-estacion")
    @PreAuthorize("hasRole('ESTACION')")
    public ResponseEntity<ApiResponse<Estacion>> miEstacion(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
            estacionService.buscarPorAdministrador(usuario.getId())));
        }
}
