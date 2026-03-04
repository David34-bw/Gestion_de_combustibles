package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model;

public class AuthResponse {
    private String token;
    private String tipo;
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;   // "USUARIO", "DISTRIBUIDOR", "ESTACION", "REGULADOR"
    // + getters


    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getApellido() {
        return apellido;
    }

    public String getToken() {
        return token;
    }

    public String getRol() {
        return rol;
    }
}


