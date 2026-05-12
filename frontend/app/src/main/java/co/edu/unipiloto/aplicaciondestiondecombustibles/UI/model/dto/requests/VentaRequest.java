package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests;

public class VentaRequest {
    private String tipoCombustible;
    private Double cantidad;
    private String observaciones;
    private String placaVehiculo;

    public VentaRequest(String tipoCombustible, Double cantidad,
                        String observaciones, String placaVehiculo) {
        this.tipoCombustible = tipoCombustible;
        this.cantidad        = cantidad;
        this.observaciones   = observaciones;
        this.placaVehiculo   = placaVehiculo;
    }

    public String getTipoCombustible()       { return tipoCombustible; }
    public Double getCantidad()              { return cantidad; }
    public String getObservaciones()         { return observaciones; }
    public String getPlacaVehiculo()         { return placaVehiculo; }
}