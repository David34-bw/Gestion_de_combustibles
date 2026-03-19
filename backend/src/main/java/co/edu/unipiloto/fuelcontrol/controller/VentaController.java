package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.request.VentaRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.dto.response.VentaResponse;
import co.edu.unipiloto.fuelcontrol.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /**
     * POST /api/ventas
     * La estación registra una venta
     */
    @PostMapping
    @PreAuthorize("hasRole('ESTACION')")
    public ResponseEntity<ApiResponse<VentaResponse>> registrar(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody VentaRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.ok("Venta registrada",
                        ventaService.registrar(usuario.getId(), request)));
    }

    /**
     * GET /api/ventas/mis-ventas
     * La estación ve su historial de ventas
     */
    @GetMapping("/mis-ventas")
    @PreAuthorize("hasRole('ESTACION')")
    public ResponseEntity<ApiResponse<List<VentaResponse>>> misVentas(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                ventaService.listarPorEstacion(usuario.getId())));
    }
}