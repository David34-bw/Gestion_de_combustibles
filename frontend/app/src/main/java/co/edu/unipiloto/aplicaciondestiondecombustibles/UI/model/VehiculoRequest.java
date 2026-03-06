package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model;

public class VehiculoRequest {
    private String placa;
    private String tipoVehiculo;
    private Boolean aplicaSubsidio;
    private String numeroRunt;
    private String marca;
    private String modelo;

    public VehiculoRequest(String placa, String tipoVehiculo,
                           Boolean aplicaSubsidio, String numeroRunt,
                           String marca, String modelo) {
        this.placa          = placa;
        this.tipoVehiculo   = tipoVehiculo;
        this.aplicaSubsidio = aplicaSubsidio;
        this.numeroRunt     = numeroRunt;
        this.marca          = marca;
        this.modelo         = modelo;
    }

    public String getPlaca()              { return placa; }
    public String getTipoVehiculo()       { return tipoVehiculo; }
    public Boolean getAplicaSubsidio()    { return aplicaSubsidio; }
    public String getNumeroRunt()         { return numeroRunt; }
    public String getMarca()              { return marca; }
    public String getModelo()             { return modelo; }
}
