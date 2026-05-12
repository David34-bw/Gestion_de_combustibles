package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.dto.response.RecompensaResponse;
import co.edu.unipiloto.fuelcontrol.service.RecompensaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recompensas")
public class RecompensaController {

    private final RecompensaService recompensaService;

    public RecompensaController(RecompensaService recompensaService) {
        this.recompensaService = recompensaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse<List<RecompensaResponse>>> listarActivas() {
        recompensaService.seedSiVacio();
        return ResponseEntity.ok(ApiResponse.ok("OK", recompensaService.listarActivas()));
    }
}
