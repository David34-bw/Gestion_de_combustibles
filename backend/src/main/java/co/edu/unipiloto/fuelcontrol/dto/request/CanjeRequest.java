package co.edu.unipiloto.fuelcontrol.dto.request;

import jakarta.validation.constraints.NotNull;

public class CanjeRequest {
    @NotNull(message = "La recompensa es requerida")
    private Long recompensaId;

    public Long getRecompensaId() { return recompensaId; }
    public void setRecompensaId(Long recompensaId) { this.recompensaId = recompensaId; }
}
