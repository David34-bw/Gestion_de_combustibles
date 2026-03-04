package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Distribuidor;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.service.DistribuidorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/distribuidores")
@RequiredArgsConstructor
public class DistribuidorController {

    private final DistribuidorService distribuidorService;

    /** GET /api/distribuidores */
    @GetMapping
    @PreAuthorize("hasAnyRole('REGULADOR', 'ESTACION')")
    public ResponseEntity<ApiResponse<List<Distribuidor>>> listarActivos() {
        return ResponseEntity.ok(ApiResponse.ok("OK", distribuidorService.listarActivos()));
    }

    /** GET /api/distribuidores/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('REGULADOR', 'DISTRIBUIDOR')")
    public ResponseEntity<ApiResponse<Distribuidor>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK", distribuidorService.buscarPorId(id)));
    }

    /** POST /api/distribuidores - REGULADOR */
    @PostMapping
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<Distribuidor>> crear(
            @RequestBody Distribuidor distribuidor,
            @RequestParam(required = false) Long representanteId) {
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Distribuidor creado",
                        distribuidorService.crear(distribuidor, representanteId)));
    }

    /** PUT /api/distribuidores/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('REGULADOR', 'DISTRIBUIDOR')")
    public ResponseEntity<ApiResponse<Distribuidor>> actualizar(
            @PathVariable Long id,
            @RequestBody Distribuidor datos) {
        return ResponseEntity.ok(ApiResponse.ok("Distribuidor actualizado",
                distribuidorService.actualizar(id, datos)));
    }

    /**
     * PATCH /api/distribuidores/{id}/stock
     * Body: { "gasolina": 500.0, "diesel": 300.0 }
     */
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('DISTRIBUIDOR', 'REGULADOR')")
    public ResponseEntity<ApiResponse<Distribuidor>> actualizarStock(
            @PathVariable Long id,
            @RequestBody Map<String, Double> stock) {
        return ResponseEntity.ok(ApiResponse.ok("Stock actualizado",
                distribuidorService.actualizarStock(id,
                        stock.get("gasolina"),
                        stock.get("diesel"))));
    }

    /** DELETE /api/distribuidores/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        distribuidorService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Distribuidor desactivado", null));
    }
}
