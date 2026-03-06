package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model;

public class Vehiculo {
    private Long id;
    private String placa;
    private String tipoVehiculo;
    private Boolean aplicaSubsidio;
    private String numeroRunt;
    private String marca;
    private String modelo;
    private String fechaRegistro;

    public Long getId()                  { return id; }
    public String getPlaca()             { return placa; }
    public String getTipoVehiculo()      { return tipoVehiculo; }
    public Boolean getAplicaSubsidio()   { return aplicaSubsidio; }
    public String getNumeroRunt()        { return numeroRunt; }
    public String getMarca()             { return marca; }
    public String getModelo()            { return modelo; }
    public String getFechaRegistro()     { return fechaRegistro; }
}
