package co.edu.unipiloto.fuelcontrol.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_combustible", nullable = false)
    private String tipoCombustible;

    @Column(nullable = false)
    private Double cantidad;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    @Column(name = "observaciones")
    private String observaciones;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "solicitudes"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_id", nullable = false)
    private Estacion estacion;

    @PrePersist
    protected void onCreate() { fechaVenta = LocalDateTime.now(); }

    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }
    public String getTipoCombustible()       { return tipoCombustible; }
    public void setTipoCombustible(String v) { this.tipoCombustible = v; }
    public Double getCantidad()              { return cantidad; }
    public void setCantidad(Double v)        { this.cantidad = v; }
    public LocalDateTime getFechaVenta()     { return fechaVenta; }
    public void setFechaVenta(LocalDateTime v){ this.fechaVenta = v; }
    public String getObservaciones()         { return observaciones; }
    public void setObservaciones(String v)   { this.observaciones = v; }
    public Estacion getEstacion()            { return estacion; }
    public void setEstacion(Estacion v)      { this.estacion = v; }
    public Usuario getUsuario()         { return usuario; }
    public void setUsuario(Usuario v)   { this.usuario = v; }
    


    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Venta v = new Venta();
        public Builder tipoCombustible(String x)  { v.tipoCombustible = x; return this; }
        public Builder cantidad(Double x)          { v.cantidad = x; return this; }
        public Builder observaciones(String x)     { v.observaciones = x; return this; }
        public Builder estacion(Estacion x)        { v.estacion = x; return this; }
        public Venta build()                       { return v; }
        public Builder usuario(Usuario x)   { v.usuario = x; return this; }
    }
}