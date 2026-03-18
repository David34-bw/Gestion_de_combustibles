package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.entregas;

public class EntregaResponse {
    private Long id;
    private String tipoCombustible;
    private Double volumen;
    private String fechaEntrega;
    private String observaciones;
    private Long distribuidorId;
    private String distribuidorNombre;
    private Long estacionId;
    private String estacionNombre;

    public Long getId()                  { return id; }
    public String getTipoCombustible()   { return tipoCombustible; }
    public Double getVolumen()           { return volumen; }
    public String getFechaEntrega()      { return fechaEntrega; }
    public String getObservaciones()     { return observaciones; }
    public Long getDistribuidorId()      { return distribuidorId; }
    public String getDistribuidorNombre(){ return distribuidorNombre; }
    public Long getEstacionId()          { return estacionId; }
    public String getEstacionNombre()    { return estacionNombre; }
}