package co.edu.unipiloto.fuelcontrol.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "autoridades_reguladoras")
public class AutoridadReguladora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    private String cargo;
    private String dependencia;

    @Column(name = "cupo_gasolina_mensual")
    private Double cupoGasolinaMensual = 100.0;

    @Column(name = "cupo_diesel_mensual")
    private Double cupoDieselMensual = 150.0;

    private Boolean activa = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @PrePersist
    protected void onCreate() { fechaRegistro = LocalDateTime.now(); }

    // ─── Getters y Setters ───────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getDependencia() { return dependencia; }
    public void setDependencia(String dependencia) { this.dependencia = dependencia; }

    public Double getCupoGasolinaMensual() { return cupoGasolinaMensual; }
    public void setCupoGasolinaMensual(Double v) { this.cupoGasolinaMensual = v; }

    public Double getCupoDieselMensual() { return cupoDieselMensual; }
    public void setCupoDieselMensual(Double v) { this.cupoDieselMensual = v; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
