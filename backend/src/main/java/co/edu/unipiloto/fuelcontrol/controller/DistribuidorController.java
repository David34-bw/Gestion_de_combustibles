package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Distribuidor;
import co.edu.unipiloto.fuelcontrol.dto.request.DistribuidorCreateRequest;
import co.edu.unipiloto.fuelcontrol.dto.request.DistribuidorUpdateRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.service.DistribuidorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distribuidores")

public class DistribuidorController {

    private final DistribuidorService distribuidorService;

    public DistribuidorController(DistribuidorService distribuidorService) {
        this.distribuidorService = distribuidorService;
    }

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
            @Valid @RequestBody DistribuidorCreateRequest request,
            @RequestParam(required = false) Long representanteId) {
        Distribuidor distribuidor = new Distribuidor();
        distribuidor.setNombre(request.getNombre());
        distribuidor.setNit(request.getNit());
        distribuidor.setCiudad(request.getCiudad());
        distribuidor.setDepartamento(request.getDepartamento());
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Distribuidor creado",
                        distribuidorService.crear(distribuidor, representanteId)));
    }

    /** PUT /api/distribuidores/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('REGULADOR', 'DISTRIBUIDOR')")
    public ResponseEntity<ApiResponse<Distribuidor>> actualizar(
            @PathVariable Long id,
            @RequestBody DistribuidorUpdateRequest datos) {
        Distribuidor distribuidor = new Distribuidor();
        distribuidor.setNombre(datos.getNombre());
        distribuidor.setCiudad(datos.getCiudad());
        distribuidor.setDepartamento(datos.getDepartamento());
        return ResponseEntity.ok(ApiResponse.ok("Distribuidor actualizado",
                distribuidorService.actualizar(id, distribuidor)));
    }

    /** DELETE /api/distribuidores/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        distribuidorService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Distribuidor desactivado", null));
    }
}
