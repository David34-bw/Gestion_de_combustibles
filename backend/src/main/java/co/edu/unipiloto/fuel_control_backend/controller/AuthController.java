package co.edu.unipiloto.fuel_control_backend.controller;


import co.edu.unipiloto.fuel_control_backend.dto.request.*;
import co.edu.unipiloto.fuel_control_backend.dto.response.ApiResponse;
import co.edu.unipiloto.fuel_control_backend.dto.response.AuthResponse;
import co.edu.unipiloto.fuel_control_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/usuario")
    public ResponseEntity<ApiResponse> registerUsuario(
            @Valid @RequestBody RegisterUsuarioRequest request) {
        return ResponseEntity.ok(authService.registerUsuario(request));
    }

    @PostMapping("/register/estacion")
    public ResponseEntity<ApiResponse> registerEstacion(
            @Valid @RequestBody RegisterEstacionRequest request) {
        return ResponseEntity.ok(authService.registerEstacion(request));
    }

    @PostMapping("/register/distribuidor")
    public ResponseEntity<ApiResponse> registerDistribuidor(
            @Valid @RequestBody RegisterDistribuidorRequest request) {
        return ResponseEntity.ok(authService.registerDistribuidor(request));
    }

    @PostMapping("/register/regulador")
    public ResponseEntity<ApiResponse> registerRegulador(
            @Valid @RequestBody RegisterReguladorRequest request) {
        return ResponseEntity.ok(authService.registerRegulador(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<AuthResponse> verify2FA(
            @Valid @RequestBody TwoFactorRequest request) {
        return ResponseEntity.ok(authService.verify2FA(request));
    }
}