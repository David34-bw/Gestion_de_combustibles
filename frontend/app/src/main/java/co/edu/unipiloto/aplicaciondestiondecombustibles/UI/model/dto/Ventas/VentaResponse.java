package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.Ventas;

public class VentaResponse {
    private Long id;
    private String tipoCombustible;
    private Double cantidad;
    private String fechaVenta;
    private String usuarioNombre;
    private String placaVehiculo;
    private String observaciones;
    private Long estacionId;
    private String estacionNombre;
    private Boolean alertaStockBajo;

    public Long getId()                  { return id; }
    public String getTipoCombustible()   { return tipoCombustible; }
    public Double getCantidad()          { return cantidad; }
    public String getFechaVenta()        { return fechaVenta; }
    public String getObservaciones()     { return observaciones; }
    public Long getEstacionId()          { return estacionId; }
    public String getEstacionNombre()    { return estacionNombre; }
    public Boolean getAlertaStockBajo()  { return alertaStockBajo; }
    public String getPlacaVehiculo()     { return placaVehiculo; }

    public String getUsuarioNombre() { return usuarioNombre; }
}