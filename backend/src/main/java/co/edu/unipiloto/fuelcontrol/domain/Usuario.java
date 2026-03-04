package co.edu.unipiloto.fuelcontrol.domain;

import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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

    @Column
    private String apellido;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(name = "numero_documento", unique = true)
    private String numeroDocumento;

    @Column(name = "telefono")
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // ── Campos vehículo (solo USUARIO) ──────────────
    @Column(name = "placa")
    private String placa;

    @Column(name = "tipo_vehiculo")
    private String tipoVehiculo;

    @Column(name = "aplica_subsidio")
    private Boolean aplicaSubsidio = false;

    @Column(name = "numero_runt")
    private String numeroRunt;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    // ── Getters y Setters ────────────────────────────
    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public String getNombre()            { return nombre; }
    public void setNombre(String v)      { this.nombre = v; }

    public String getApellido()          { return apellido; }
    public void setApellido(String v)    { this.apellido = v; }

    public String getEmail()             { return email; }
    public void setEmail(String v)       { this.email = v; }

    public void setPassword(String v)    { this.password = v; }

    public String getNumeroDocumento()   { return numeroDocumento; }
    public void setNumeroDocumento(String v) { this.numeroDocumento = v; }

    public String getTelefono()          { return telefono; }
    public void setTelefono(String v)    { this.telefono = v; }

    public Rol getRol()                  { return rol; }
    public void setRol(Rol v)            { this.rol = v; }

    public Boolean getActivo()           { return activo; }
    public void setActivo(Boolean v)     { this.activo = v; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    public String getPlaca()             { return placa; }
    public void setPlaca(String v)       { this.placa = v; }

    public String getTipoVehiculo()      { return tipoVehiculo; }
    public void setTipoVehiculo(String v){ this.tipoVehiculo = v; }

    public Boolean getAplicaSubsidio()   { return aplicaSubsidio; }
    public void setAplicaSubsidio(Boolean v) { this.aplicaSubsidio = v; }

    public String getNumeroRunt()        { return numeroRunt; }
    public void setNumeroRunt(String v)  { this.numeroRunt = v; }

    // ── Builder ──────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Usuario u = new Usuario();
        public Builder nombre(String v)           { u.nombre = v; return this; }
        public Builder apellido(String v)         { u.apellido = v; return this; }
        public Builder email(String v)            { u.email = v; return this; }
        public Builder password(String v)         { u.password = v; return this; }
        public Builder numeroDocumento(String v)  { u.numeroDocumento = v; return this; }
        public Builder telefono(String v)         { u.telefono = v; return this; }
        public Builder rol(Rol v)                 { u.rol = v; return this; }
        public Builder activo(Boolean v)          { u.activo = v; return this; }
        public Builder placa(String v)            { u.placa = v; return this; }
        public Builder tipoVehiculo(String v)     { u.tipoVehiculo = v; return this; }
        public Builder aplicaSubsidio(Boolean v)  { u.aplicaSubsidio = v; return this; }
        public Builder numeroRunt(String v)       { u.numeroRunt = v; return this; }
        public Usuario build()                    { return u; }
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