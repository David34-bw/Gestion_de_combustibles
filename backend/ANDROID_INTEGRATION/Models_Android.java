// ═══════════════════════════════════════════════════════════════
// MODELOS ANDROID - Coloca cada clase en su propio archivo .java
// Ruta: app/src/main/java/co/edu/unipiloto/.../model/
// ═══════════════════════════════════════════════════════════════

// ─── ApiResponse.java ──────────────────────────────────────────
package co.edu.unipiloto.aplicaciongestiondecombustibles.model;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData()         { return data; }
}

─── AuthResponse.java ─────────────────────────────────────────
public class AuthResponse {
    private String token;
    private String tipo;
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;   // "USUARIO", "DISTRIBUIDOR", "ESTACION", "REGULADOR"
    // + getters
}

─── LoginRequest.java ─────────────────────────────────────────
public class LoginRequest {
    private String email;
    private String password;
    public LoginRequest(String email, String password) { ... }
}

─── RegisterRequest.java ──────────────────────────────────────
public class RegisterRequest {
    private String nombre, apellido, email, password;
    private String numeroDocumento, telefono;
    private String rol;   // "USUARIO", "DISTRIBUIDOR", "ESTACION", "REGULADOR"
}

─── Usuario.java ──────────────────────────────────────────────
public class Usuario {
    private Long id;
    private String nombre, apellido, email, telefono, rol;
    private Boolean activo;
}

─── Estacion.java ─────────────────────────────────────────────
public class Estacion {
    private Long id;
    private String nombre, direccion, ciudad, nit;
    private Double stockGasolina, stockDiesel;
    private Boolean activa;
}

─── Distribuidor.java ─────────────────────────────────────────
public class Distribuidor {
    private Long id;
    private String nombre, nit, ciudad;
    private Double stockGasolina, stockDiesel;
    private Boolean activo;
}

─── SolicitudCombustible.java ─────────────────────────────────
public class SolicitudCombustible {
    private Long id;
    private String tipoCombustible;   // "GASOLINA" o "DIESEL"
    private Double cantidad;
    private String estado;            // "PENDIENTE", "APROBADA", "RECHAZADA", "ENTREGADA"
    private String observaciones;
    private String motivoRechazo;
    private String fechaSolicitud;
    private Long usuarioId;
    private String usuarioNombre;
    private Long estacionId;
    private String estacionNombre;
}

─── SolicitudRequest.java ─────────────────────────────────────
public class SolicitudRequest {
    private String tipoCombustible;
    private Double cantidad;
    private String observaciones;
    private Long estacionId;
}

─── ResolucionRequest.java ────────────────────────────────────
public class ResolucionRequest {
    private String estado;         // "APROBADA" o "RECHAZADA"
    private String motivoRechazo;
    private Long distribuidorId;
    private Long estacionId;
}
