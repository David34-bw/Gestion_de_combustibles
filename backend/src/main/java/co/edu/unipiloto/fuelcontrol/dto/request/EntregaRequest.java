package co.edu.unipiloto.fuelcontrol.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class EntregaRequest {

    @NotBlank(message = "El tipo de combustible es requerido")
    private String tipoCombustible;

    @NotNull(message = "El volumen es requerido")
    @Positive(message = "El volumen debe ser mayor a 0")
    private Double volumen;

    @NotNull(message = "La estación destino es requerida")
    private Long estacionId;

    private String observaciones;

    public String getTipoCombustible()       { return tipoCombustible; }
    public void setTipoCombustible(String v) { this.tipoCombustible = v; }
    public Double getVolumen()               { return volumen; }
    public void setVolumen(Double v)         { this.volumen = v; }
    public Long getEstacionId()              { return estacionId; }
    public void setEstacionId(Long v)        { this.estacionId = v; }
    public String getObservaciones()         { return observaciones; }
    public void setObservaciones(String v)   { this.observaciones = v; }
}