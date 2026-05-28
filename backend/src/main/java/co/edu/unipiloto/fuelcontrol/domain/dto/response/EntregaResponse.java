package co.edu.unipiloto.fuelcontrol.domain.dto.response;


import java.time.LocalDateTime;

public class EntregaResponse {
    private Long id;
    private String tipoCombustible;
    private Double volumen;
    private LocalDateTime fechaEntrega;
    private String observaciones;
    private Long distribuidorId;
    private String distribuidorNombre;
    private Long estacionId;
    private String estacionNombre;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final EntregaResponse r = new EntregaResponse();
        public Builder id(Long v)                   { r.id = v; return this; }
        public Builder tipoCombustible(String v)    { r.tipoCombustible = v; return this; }
        public Builder volumen(Double v)            { r.volumen = v; return this; }
        public Builder fechaEntrega(LocalDateTime v){ r.fechaEntrega = v; return this; }
        public Builder observaciones(String v)      { r.observaciones = v; return this; }
        public Builder distribuidorId(Long v)       { r.distribuidorId = v; return this; }
        public Builder distribuidorNombre(String v) { r.distribuidorNombre = v; return this; }
        public Builder estacionId(Long v)           { r.estacionId = v; return this; }
        public Builder estacionNombre(String v)     { r.estacionNombre = v; return this; }
        public EntregaResponse build()              { return r; }
    }

    public Long getId()                      { return id; }
    public String getTipoCombustible()       { return tipoCombustible; }
    public Double getVolumen()               { return volumen; }
    public LocalDateTime getFechaEntrega()   { return fechaEntrega; }
    public String getObservaciones()         { return observaciones; }
    public Long getDistribuidorId()          { return distribuidorId; }
    public String getDistribuidorNombre()    { return distribuidorNombre; }
    public Long getEstacionId()              { return estacionId; }
    public String getEstacionNombre()        { return estacionNombre; }
}