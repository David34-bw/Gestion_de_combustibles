package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.AutoridadReguladora;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.service.AutoridadReguladoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autoridades")
@RequiredArgsConstructor
@PreAuthorize("hasRole('REGULADOR')")
public class AutoridadReguladoraController {

    private final AutoridadReguladoraService autoridadService;

    /** GET /api/autoridades */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AutoridadReguladora>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok("OK", autoridadService.listarTodas()));
    }

    /** GET /api/autoridades/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AutoridadReguladora>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK", autoridadService.buscarPorId(id)));
    }

    /** POST /api/autoridades?usuarioId=X */
    @PostMapping
    public ResponseEntity<ApiResponse<AutoridadReguladora>> crear(
            @RequestBody AutoridadReguladora autoridad,
            @RequestParam Long usuarioId) {
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Autoridad creada", autoridadService.crear(autoridad, usuarioId)));
    }

    /** PUT /api/autoridades/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AutoridadReguladora>> actualizar(
            @PathVariable Long id,
            @RequestBody AutoridadReguladora datos) {
        return ResponseEntity.ok(ApiResponse.ok("Actualizado",
                autoridadService.actualizar(id, datos)));
    }

    /** DELETE /api/autoridades/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        autoridadService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Desactivado", null));
    }
}
