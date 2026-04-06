package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.precios;

public class HistorialPrecio {
    private Long id;
    private String zona;
    private String tipoCombustible;
    private Double precioAnterior;
    private Double precioNuevo;
    private String fechaCambio;
    private String cambiadoPor;

    public Long getId()                  { return id; }
    public String getZona()              { return zona; }
    public String getTipoCombustible()   { return tipoCombustible; }
    public Double getPrecioAnterior()    { return precioAnterior; }
    public Double getPrecioNuevo()       { return precioNuevo; }
    public String getFechaCambio()       { return fechaCambio; }
    public String getCambiadoPor()       { return cambiadoPor; }
}