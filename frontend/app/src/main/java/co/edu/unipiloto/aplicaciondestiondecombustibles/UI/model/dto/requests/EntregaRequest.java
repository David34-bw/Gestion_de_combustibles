package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests;

public class EntregaRequest {
    private String tipoCombustible;
    private Double volumen;
    private Long estacionId;
    private String observaciones;

    public EntregaRequest(String tipoCombustible, Double volumen,
                          Long estacionId, String observaciones) {
        this.tipoCombustible = tipoCombustible;
        this.volumen         = volumen;
        this.estacionId      = estacionId;
        this.observaciones   = observaciones;
    }

    public String getTipoCombustible()       { return tipoCombustible; }
    public Double getVolumen()               { return volumen; }
    public Long getEstacionId()              { return estacionId; }
    public String getObservaciones()         { return observaciones; }
}