package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.domain.Vehiculo;
import co.edu.unipiloto.fuelcontrol.dto.request.VehiculoRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    /**
     * POST /api/vehiculos
     * Registra un vehículo para el usuario autenticado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Vehiculo>> registrar(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody VehiculoRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Vehículo registrado",
                        vehiculoService.registrar(usuario.getId(), request)));
    }

    /**
     * GET /api/vehiculos/mis-vehiculos
     * Lista los vehículos del usuario autenticado
     */
    @GetMapping("/mis-vehiculos")
    public ResponseEntity<ApiResponse<List<Vehiculo>>> misVehiculos(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(
                ApiResponse.ok("OK", vehiculoService.listarPorUsuario(usuario.getId())));
    }

    /**
     * DELETE /api/vehiculos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Vehículo eliminado", null));
    }
}
