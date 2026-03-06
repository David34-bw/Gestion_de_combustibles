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

    @Column(name = "telefono_contacto")
    private String telefonoContacto;

    @Column(name = "email_contacto")
    private String emailContacto;

    private String ciudad;

    @Column(name = "capacidad_tanque")
    private Double capacidadTanque;

    @Column(name = "stock_gasolina")
    private Double stockGasolina = 0.0;

    @Column(name = "stock_diesel")
    private Double stockDiesel = 0.0;

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

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String v) { this.telefonoContacto = v; }

    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String v) { this.emailContacto = v; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Double getCapacidadTanque() { return capacidadTanque; }
    public void setCapacidadTanque(Double v) { this.capacidadTanque = v; }

    public Double getStockGasolina() { return stockGasolina; }
    public void setStockGasolina(Double v) { this.stockGasolina = v; }

    public Double getStockDiesel() { return stockDiesel; }
    public void setStockDiesel(Double v) { this.stockDiesel = v; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    public Usuario getRepresentante() { return representante; }
    public void setRepresentante(Usuario representante) { this.representante = representante; }
}
