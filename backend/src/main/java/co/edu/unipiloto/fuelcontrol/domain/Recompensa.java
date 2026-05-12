package co.edu.unipiloto.fuelcontrol.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recompensas")
public class Recompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "costo_puntos", nullable = false)
    private Integer costoPuntos;

    @Column(name = "porcentaje_descuento", nullable = false)
    private Integer porcentajeDescuento;

    @Column(nullable = false)
    private Boolean activo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getCostoPuntos() { return costoPuntos; }
    public void setCostoPuntos(Integer costoPuntos) { this.costoPuntos = costoPuntos; }
    public Integer getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(Integer porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
