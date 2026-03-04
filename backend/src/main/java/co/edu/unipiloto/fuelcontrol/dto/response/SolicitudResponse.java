package co.edu.unipiloto.fuelcontrol.dto.response;

import co.edu.unipiloto.fuelcontrol.domain.enums.EstadoSolicitud;

import java.time.LocalDateTime;

public class SolicitudResponse {
    private Long id;
    private String tipoCombustible;
    private Double cantidad;
    private EstadoSolicitud estado;
    private String observaciones;
    private String motivoRechazo;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaResolucion;
    private Long usuarioId;
    private String usuarioNombre;
    private Long estacionId;
    private String estacionNombre;
    private Long distribuidorId;
    private String distribuidorNombre;
    private String aprobadoPorNombre;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SolicitudResponse r = new SolicitudResponse();
        public Builder id(Long v)                   { r.id = v; return this; }
        public Builder tipoCombustible(String v)    { r.tipoCombustible = v; return this; }
        public Builder cantidad(Double v)           { r.cantidad = v; return this; }
        public Builder estado(EstadoSolicitud v)    { r.estado = v; return this; }
        public Builder observaciones(String v)      { r.observaciones = v; return this; }
        public Builder motivoRechazo(String v)      { r.motivoRechazo = v; return this; }
        public Builder fechaSolicitud(LocalDateTime v)  { r.fechaSolicitud = v; return this; }
        public Builder fechaResolucion(LocalDateTime v) { r.fechaResolucion = v; return this; }
        public Builder usuarioId(Long v)            { r.usuarioId = v; return this; }
        public Builder usuarioNombre(String v)      { r.usuarioNombre = v; return this; }
        public Builder estacionId(Long v)           { r.estacionId = v; return this; }
        public Builder estacionNombre(String v)     { r.estacionNombre = v; return this; }
        public Builder distribuidorId(Long v)       { r.distribuidorId = v; return this; }
        public Builder distribuidorNombre(String v) { r.distribuidorNombre = v; return this; }
        public Builder aprobadoPorNombre(String v)  { r.aprobadoPorNombre = v; return this; }
        public SolicitudResponse build()            { return r; }
    }

    // Getters
    public Long getId()                      { return id; }
    public String getTipoCombustible()       { return tipoCombustible; }
    public Double getCantidad()              { return cantidad; }
    public EstadoSolicitud getEstado()       { return estado; }
    public String getObservaciones()         { return observaciones; }
    public String getMotivoRechazo()         { return motivoRechazo; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public LocalDateTime getFechaResolucion(){ return fechaResolucion; }
    public Long getUsuarioId()               { return usuarioId; }
    public String getUsuarioNombre()         { return usuarioNombre; }
    public Long getEstacionId()              { return estacionId; }
    public String getEstacionNombre()        { return estacionNombre; }
    public Long getDistribuidorId()          { return distribuidorId; }
    public String getDistribuidorNombre()    { return distribuidorNombre; }
    public String getAprobadoPorNombre()     { return aprobadoPorNombre; }
}
