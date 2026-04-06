package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests;

public class PrecioUpdateRequest {
    private String zona;
    private String tipoCombustible;
    private Double precio;

    public PrecioUpdateRequest(String zona, String tipoCombustible, Double precio) {
        this.zona            = zona;
        this.tipoCombustible = tipoCombustible;
        this.precio          = precio;
    }

    public String getZona()              { return zona; }
    public String getTipoCombustible()   { return tipoCombustible; }
    public Double getPrecio()            { return precio; }
}