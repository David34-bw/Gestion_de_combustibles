package co.edu.unipiloto.fuelcontrol.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class VentaRequest {

    @NotBlank(message = "El tipo de combustible es requerido")
    private String tipoCombustible;

    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Double cantidad;
    // Agrega este campo:
    private String placaVehiculo;

    private Long estacionId;


    private String observaciones;

    public String getTipoCombustible()       { return tipoCombustible; }
    public void setTipoCombustible(String v) { this.tipoCombustible = v; }
    public Double getCantidad()              { return cantidad; }
    public void setCantidad(Double v)        { this.cantidad = v; }
    public String getObservaciones()         { return observaciones; }
    public void setObservaciones(String v)   { this.observaciones = v; }
    public String getPlacaVehiculo()       { return placaVehiculo; }
public void setPlacaVehiculo(String v) { this.placaVehiculo = v; }
    public Long getEstacionId()              { return estacionId; }
    public void setEstacionId(Long v)        { this.estacionId = v; }
}
