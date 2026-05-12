package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests;

public class CanjeRequest {
    private Long recompensaId;

    public CanjeRequest(Long recompensaId) {
        this.recompensaId = recompensaId;
    }

    public Long getRecompensaId() { return recompensaId; }
    public void setRecompensaId(Long recompensaId) { this.recompensaId = recompensaId; }
}
