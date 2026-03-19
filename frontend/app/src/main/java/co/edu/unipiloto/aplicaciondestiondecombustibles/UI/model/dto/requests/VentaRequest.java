package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests;

public class VentaRequest {
    private String tipoCombustible;
    private Double cantidad;
    private String observaciones;

    public VentaRequest(String tipoCombustible, Double cantidad, String observaciones) {
        this.tipoCombustible = tipoCombustible;
        this.cantidad        = cantidad;
        this.observaciones   = observaciones;
    }

    public String getTipoCombustible()       { return tipoCombustible; }
    public Double getCantidad()              { return cantidad; }
    public String getObservaciones()         { return observaciones; }
}