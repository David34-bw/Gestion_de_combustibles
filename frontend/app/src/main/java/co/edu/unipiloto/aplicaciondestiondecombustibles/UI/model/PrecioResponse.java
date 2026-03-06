package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model;

public class PrecioResponse {
    private String zona;
    private String tipoCombustible;
    private String tipoVehiculo;
    private Double precioBase;
    private Double descuentoPct;
    private Double precioFinal;
    private String unidad;

    public String getZona()            { return zona; }
    public String getTipoCombustible() { return tipoCombustible; }
    public String getTipoVehiculo()    { return tipoVehiculo; }
    public Double getPrecioBase()      { return precioBase; }
    public Double getDescuentoPct()    { return descuentoPct; }
    public Double getPrecioFinal()     { return precioFinal; }
    public String getUnidad()          { return unidad; }
}
