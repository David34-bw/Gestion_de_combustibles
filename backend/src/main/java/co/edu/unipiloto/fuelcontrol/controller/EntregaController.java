package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.request.EntregaRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.dto.response.EntregaResponse;
import co.edu.unipiloto.fuelcontrol.service.EntregaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    /**
     * POST /api/entregas
     * El distribuidor registra una entrega
     */
    @PostMapping
    @PreAuthorize("hasRole('DISTRIBUIDOR')")
    public ResponseEntity<ApiResponse<EntregaResponse>> registrar(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody EntregaRequest request) {

        // Buscar el distribuidor asociado al usuario autenticado
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Entrega registrada",
                        entregaService.registrarPorUsuario(usuario.getId(), request)));
    }

    /**
     * GET /api/entregas/mis-entregas
     * El distribuidor ve su historial
     */
    @GetMapping("/mis-entregas")
    @PreAuthorize("hasRole('DISTRIBUIDOR')")
    public ResponseEntity<ApiResponse<List<EntregaResponse>>> misEntregas(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                entregaService.listarPorUsuario(usuario.getId())));
    }

    /**
     * GET /api/entregas/estacion/{id}
     * La estación ve las entregas que recibió
     */
    @GetMapping("/estacion/{id}")
    @PreAuthorize("hasAnyRole('ESTACION', 'REGULADOR')")
    public ResponseEntity<ApiResponse<List<EntregaResponse>>> porEstacion(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                entregaService.listarPorEstacion(id)));
    }
}