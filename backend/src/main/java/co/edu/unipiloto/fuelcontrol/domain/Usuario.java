package co.edu.unipiloto.fuelcontrol.domain;

import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(name = "numero_documento", unique = true)
    private String numeroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "puntos_acumulados", nullable = false, columnDefinition = "integer default 0")
    private Integer puntosAcumulados = 0;

    // ── Getters y Setters ────────────────────────────
    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }
    public String getNombre()                { return nombre; }
    public void setNombre(String v)          { this.nombre = v; }
    public String getEmail()                 { return email; }
    public void setEmail(String v)           { this.email = v; }
    public void setPassword(String v)        { this.password = v; }
    public String getNumeroDocumento()       { return numeroDocumento; }
    public void setNumeroDocumento(String v) { this.numeroDocumento = v; }
    public Rol getRol()                      { return rol; }
    public void setRol(Rol v)                { this.rol = v; }
    public Boolean getActivo()               { return activo; }
    public void setActivo(Boolean v)         { this.activo = v; }
    public Integer getPuntosAcumulados()     { return puntosAcumulados; }
    public void setPuntosAcumulados(Integer v) { this.puntosAcumulados = v; }

    // ── Builder ──────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Usuario u = new Usuario();
        public Builder nombre(String v)          { u.nombre = v; return this; }
        public Builder email(String v)           { u.email = v; return this; }
        public Builder password(String v)        { u.password = v; return this; }
        public Builder numeroDocumento(String v) { u.numeroDocumento = v; return this; }
        public Builder rol(Rol v)                { u.rol = v; return this; }
        public Builder activo(Boolean v)         { u.activo = v; return this; }
        public Builder puntosAcumulados(Integer v) { u.puntosAcumulados = v; return this; }
        public Usuario build()                   { return u; }
    }

    // ── UserDetails ──────────────────────────────────
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override public String getPassword()              { return password; }
    @Override public String getUsername()              { return email; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return activo; }
}
