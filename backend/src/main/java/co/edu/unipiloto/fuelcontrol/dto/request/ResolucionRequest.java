package co.edu.unipiloto.fuelcontrol.dto.request;

import co.edu.unipiloto.fuelcontrol.domain.enums.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public class ResolucionRequest {
    @NotNull private EstadoSolicitud estado;
    private String motivoRechazo;
    private Long distribuidorId;
    private Long estacionId;

    public EstadoSolicitud getEstado()       { return estado; }
    public void setEstado(EstadoSolicitud v) { this.estado = v; }
    public String getMotivoRechazo()         { return motivoRechazo; }
    public void setMotivoRechazo(String v)   { this.motivoRechazo = v; }
    public Long getDistribuidorId()          { return distribuidorId; }
    public void setDistribuidorId(Long v)    { this.distribuidorId = v; }
    public Long getEstacionId()              { return estacionId; }
    public void setEstacionId(Long v)        { this.estacionId = v; }
}
