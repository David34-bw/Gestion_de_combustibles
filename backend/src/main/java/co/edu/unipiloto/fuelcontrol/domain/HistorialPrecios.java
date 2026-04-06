package co.edu.unipiloto.fuelcontrol.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_precios")
public class HistorialPrecios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String zona;

    @Column(name = "tipo_combustible", nullable = false)
    private String tipoCombustible;

    @Column(name = "precio_anterior")
    private Double precioAnterior;

    @Column(name = "precio_nuevo", nullable = false)
    private Double precioNuevo;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    @Column(name = "cambiado_por")
    private String cambiadoPor;

    @PrePersist
    protected void onCreate() { fechaCambio = LocalDateTime.now(); }

    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }
    public String getZona()                  { return zona; }
    public void setZona(String v)            { this.zona = v; }
    public String getTipoCombustible()       { return tipoCombustible; }
    public void setTipoCombustible(String v) { this.tipoCombustible = v; }
    public Double getPrecioAnterior()        { return precioAnterior; }
    public void setPrecioAnterior(Double v)  { this.precioAnterior = v; }
    public Double getPrecioNuevo()           { return precioNuevo; }
    public void setPrecioNuevo(Double v)     { this.precioNuevo = v; }
    public LocalDateTime getFechaCambio()    { return fechaCambio; }
    public String getCambiadoPor()           { return cambiadoPor; }
    public void setCambiadoPor(String v)     { this.cambiadoPor = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final HistorialPrecios h = new HistorialPrecios();
        public Builder zona(String v)             { h.zona = v; return this; }
        public Builder tipoCombustible(String v)  { h.tipoCombustible = v; return this; }
        public Builder precioAnterior(Double v)   { h.precioAnterior = v; return this; }
        public Builder precioNuevo(Double v)      { h.precioNuevo = v; return this; }
        public Builder cambiadoPor(String v)      { h.cambiadoPor = v; return this; }
        public HistorialPrecios build()           { return h; }
    }
}