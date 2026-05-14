package co.edu.unipiloto.fuelcontrol.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "distribuidores")
public class Distribuidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nit;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "venta_gasolina")
    private Double venta_gasolina = 0.0;

    @Column(name = "venta_diesel")
    private Double venta_diesel = 0.0;

    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "solicitudes"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representante_id")
    private Usuario representante;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "solicitudes"})
    @OneToMany(mappedBy = "distribuidor", cascade = CascadeType.ALL)
    private List<SolicitudCombustible> solicitudes;

    @PrePersist
    protected void onCreate() { fechaRegistro = LocalDateTime.now(); }

    // ─── Getters y Setters ───────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Double getVentaGasolina() { return venta_gasolina; }
    public void setVentaGasolina(Double v) { this.venta_gasolina = v; }

    public Double getVentaDiesel() { return venta_diesel; }
    public void setVentaDiesel(Double v) { this.venta_diesel = v; }
    
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String v) { this.departamento = v; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    public Usuario getRepresentante() { return representante; }
    public void setRepresentante(Usuario representante) { this.representante = representante; }
}
