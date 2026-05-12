package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.dto.request.CanjeRequest;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.dto.response.CanjeResponse;
import co.edu.unipiloto.fuelcontrol.dto.response.PuntosResponse;
import co.edu.unipiloto.fuelcontrol.service.PuntosService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/puntos")
public class PuntosController {

    private final PuntosService puntosService;

    public PuntosController(PuntosService puntosService) {
        this.puntosService = puntosService;
    }

    @GetMapping("/saldo")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse<PuntosResponse>> saldo(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                puntosService.obtenerPuntos(usuario.getId())));
    }

    @GetMapping("/canjes")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse<List<CanjeResponse>>> canjes(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok("OK",
                puntosService.listarCanjes(usuario.getId())));
    }

    @PostMapping("/canjes")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse<CanjeResponse>> canjear(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CanjeRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.ok("Canje exitoso",
                puntosService.canjear(usuario.getId(), request.getRecompensaId())));
    }
}
