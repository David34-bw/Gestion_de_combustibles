package co.edu.unipiloto.fuelcontrol.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reguladores")
public class Regulador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nit", unique = true)
    private String nit;

    @Column(name = "codigo_entidad")
    private String codigoEntidad;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "dependencia")
    private String dependencia;

    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "solicitudes"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @PrePersist
    protected void onCreate() { fechaRegistro = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getCodigoEntidad() { return codigoEntidad; }
    public void setCodigoEntidad(String codigoEntidad) { this.codigoEntidad = codigoEntidad; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getDependencia() { return dependencia; }
    public void setDependencia(String dependencia) { this.dependencia = dependencia; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
