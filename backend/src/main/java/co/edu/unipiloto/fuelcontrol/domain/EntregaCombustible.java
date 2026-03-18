package co.edu.unipiloto.fuelcontrol.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "entregas_combustible")
public class EntregaCombustible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_combustible", nullable = false)
    private String tipoCombustible;

    @Column(nullable = false)
    private Double volumen;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "observaciones")
    private String observaciones;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distribuidor_id", nullable = false)
    private Distribuidor distribuidor;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "solicitudes"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_id", nullable = false)
    private Estacion estacion;

    @PrePersist
    protected void onCreate() { fechaEntrega = LocalDateTime.now(); }

    // Getters y Setters
    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }
    public String getTipoCombustible()       { return tipoCombustible; }
    public void setTipoCombustible(String v) { this.tipoCombustible = v; }
    public Double getVolumen()               { return volumen; }
    public void setVolumen(Double v)         { this.volumen = v; }
    public LocalDateTime getFechaEntrega()   { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime v) { this.fechaEntrega = v; }
    public String getObservaciones()         { return observaciones; }
    public void setObservaciones(String v)   { this.observaciones = v; }
    public Distribuidor getDistribuidor()    { return distribuidor; }
    public void setDistribuidor(Distribuidor v) { this.distribuidor = v; }
    public Estacion getEstacion()            { return estacion; }
    public void setEstacion(Estacion v)      { this.estacion = v; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final EntregaCombustible e = new EntregaCombustible();
        public Builder tipoCombustible(String v)  { e.tipoCombustible = v; return this; }
        public Builder volumen(Double v)          { e.volumen = v; return this; }
        public Builder observaciones(String v)    { e.observaciones = v; return this; }
        public Builder distribuidor(Distribuidor v){ e.distribuidor = v; return this; }
        public Builder estacion(Estacion v)       { e.estacion = v; return this; }
        public EntregaCombustible build()         { return e; }
    }
}