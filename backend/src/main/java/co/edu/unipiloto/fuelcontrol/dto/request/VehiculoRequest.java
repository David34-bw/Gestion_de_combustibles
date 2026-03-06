package co.edu.unipiloto.fuelcontrol.dto.request;

import jakarta.validation.constraints.NotBlank;

public class VehiculoRequest {

    @NotBlank(message = "La placa es requerida")
    private String placa;

    @NotBlank(message = "El tipo de vehículo es requerido")
    private String tipoVehiculo;

    private Boolean aplicaSubsidio = false;
    private String numeroRunt;
    private String marca;
    private String modelo;

    public String getPlaca()              { return placa; }
    public void setPlaca(String v)        { this.placa = v; }
    public String getTipoVehiculo()       { return tipoVehiculo; }
    public void setTipoVehiculo(String v) { this.tipoVehiculo = v; }
    public Boolean getAplicaSubsidio()    { return aplicaSubsidio; }
    public void setAplicaSubsidio(Boolean v) { this.aplicaSubsidio = v; }
    public String getNumeroRunt()         { return numeroRunt; }
    public void setNumeroRunt(String v)   { this.numeroRunt = v; }
    public String getMarca()              { return marca; }
    public void setMarca(String v)        { this.marca = v; }
    public String getModelo()             { return modelo; }
    public void setModelo(String v)       { this.modelo = v; }
}
