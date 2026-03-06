package co.edu.unipiloto.fuelcontrol.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "estaciones")
public class Estacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false)
    private String direccion;

    private String ciudad;
    private String departamento;

    @Column(name = "nit", unique = true)
    private String nit;

    @Column(name = "capacidad_gasolina")
    private Double capacidadGasolina;

    @Column(name = "capacidad_diesel")
    private Double capacidadDiesel;

    @Column(name = "stock_gasolina")
    private Double stockGasolina = 0.0;

    @Column(name = "stock_diesel")
    private Double stockDiesel = 0.0;

    private Boolean activa = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "solicitudes"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_id")
    private Usuario administrador;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "solicitudes"})
    @OneToMany(mappedBy = "estacion", cascade = CascadeType.ALL)
    private List<SolicitudCombustible> solicitudes;

    @PrePersist
    protected void onCreate() { fechaRegistro = LocalDateTime.now(); }

    // ─── Getters y Setters ───────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public Double getCapacidadGasolina() { return capacidadGasolina; }
    public void setCapacidadGasolina(Double v) { this.capacidadGasolina = v; }

    public Double getCapacidadDiesel() { return capacidadDiesel; }
    public void setCapacidadDiesel(Double v) { this.capacidadDiesel = v; }

    public Double getStockGasolina() { return stockGasolina; }
    public void setStockGasolina(Double v) { this.stockGasolina = v; }

    public Double getStockDiesel() { return stockDiesel; }
    public void setStockDiesel(Double v) { this.stockDiesel = v; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    public Usuario getAdministrador() { return administrador; }
    public void setAdministrador(Usuario administrador) { this.administrador = administrador; }
}
