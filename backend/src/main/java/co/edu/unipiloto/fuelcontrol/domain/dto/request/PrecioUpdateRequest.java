package co.edu.unipiloto.fuelcontrol.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PrecioUpdateRequest {

    @NotBlank(message = "La zona es requerida")
    private String zona;

    @NotBlank(message = "El tipo de combustible es requerido")
    private String tipoCombustible;

    @NotNull(message = "El precio es requerido")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    public String getZona()                  { return zona; }
    public void setZona(String v)            { this.zona = v; }
    public String getTipoCombustible()       { return tipoCombustible; }
    public void setTipoCombustible(String v) { this.tipoCombustible = v; }
    public Double getPrecio()                { return precio; }
    public void setPrecio(Double v)          { this.precio = v; }
}