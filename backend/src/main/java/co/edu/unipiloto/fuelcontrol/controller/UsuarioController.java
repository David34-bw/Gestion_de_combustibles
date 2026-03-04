package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /** GET /api/usuarios - Solo REGULADOR */
    @GetMapping
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<List<Usuario>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok("OK", usuarioService.listarTodos()));
    }

    /** GET /api/usuarios/rol/{rol} */
    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<List<Usuario>>> listarPorRol(@PathVariable Rol rol) {
        return ResponseEntity.ok(ApiResponse.ok("OK", usuarioService.listarPorRol(rol)));
    }

    /** GET /api/usuarios/me - Usuario actual */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Usuario>> miPerfil(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok("OK", usuario));
    }

    /** GET /api/usuarios/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('REGULADOR') or authentication.principal.id == #id")
    public ResponseEntity<ApiResponse<Usuario>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK", usuarioService.buscarPorId(id)));
    }

    /** PUT /api/usuarios/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('REGULADOR') or authentication.principal.id == #id")
    public ResponseEntity<ApiResponse<Usuario>> actualizar(
            @PathVariable Long id,
            @RequestBody Usuario datos) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado",
                usuarioService.actualizar(id, datos)));
    }

    /** DELETE /api/usuarios/{id} - Desactiva (no borra) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('REGULADOR')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario desactivado", null));
    }
}
