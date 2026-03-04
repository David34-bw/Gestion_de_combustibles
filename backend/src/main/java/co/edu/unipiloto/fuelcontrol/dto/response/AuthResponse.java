package co.edu.unipiloto.fuelcontrol.dto.response;

import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;

public class AuthResponse {
    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuthResponse r = new AuthResponse();
        public Builder token(String v)    { r.token = v; return this; }
        public Builder tipo(String v)     { r.tipo = v; return this; }
        public Builder id(Long v)         { r.id = v; return this; }
        public Builder nombre(String v)   { r.nombre = v; return this; }
        public Builder apellido(String v) { r.apellido = v; return this; }
        public Builder email(String v)    { r.email = v; return this; }
        public Builder rol(Rol v)         { r.rol = v; return this; }
        public AuthResponse build()       { return r; }
    }

    public String getToken()    { return token; }
    public String getTipo()     { return tipo; }
    public Long getId()         { return id; }
    public String getNombre()   { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail()    { return email; }
    public Rol getRol()         { return rol; }
}
