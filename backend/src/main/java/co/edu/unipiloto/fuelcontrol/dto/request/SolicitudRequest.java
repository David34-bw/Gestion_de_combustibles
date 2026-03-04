package co.edu.unipiloto.fuelcontrol.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SolicitudRequest {
    @NotBlank private String tipoCombustible;
    @NotNull @Positive private Double cantidad;
    private String observaciones;
    private Long estacionId;

    public String getTipoCombustible()       { return tipoCombustible; }
    public void setTipoCombustible(String v) { this.tipoCombustible = v; }
    public Double getCantidad()              { return cantidad; }
    public void setCantidad(Double v)        { this.cantidad = v; }
    public String getObservaciones()         { return observaciones; }
    public void setObservaciones(String v)   { this.observaciones = v; }
    public Long getEstacionId()              { return estacionId; }
    public void setEstacionId(Long v)        { this.estacionId = v; }
}
