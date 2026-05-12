package co.edu.unipiloto.fuelcontrol.controller;

import co.edu.unipiloto.fuelcontrol.domain.Usuario;
import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import co.edu.unipiloto.fuelcontrol.dto.request.UsuarioUpdateDTO;
import co.edu.unipiloto.fuelcontrol.dto.response.ApiResponse;
import co.edu.unipiloto.fuelcontrol.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios") 
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<Usuario>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok("OK", usuarioService.listarTodos()));
    }

    /** GET /api/usuarios/rol/{rol} */
    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
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
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Usuario>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("OK", usuarioService.buscarPorId(id)));
    }

    /** PUT /api/usuarios/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Usuario>> actualizar(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateDTO datos) { // Cambiado a DTO
        return ResponseEntity.ok(ApiResponse.ok("Datos actualizados correctamente",
                usuarioService.actualizar(id, datos)));
    }

    @PutMapping("/{id}/desactivar") 
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Desactivado", null));
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        usuarioService.activar(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario activado con Ã©xito", null));
    }

    @DeleteMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado", null));
    }

}
