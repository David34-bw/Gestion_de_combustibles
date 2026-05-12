package co.edu.unipiloto.fuelcontrol.dto.response;

import java.time.LocalDateTime;

public class CanjeResponse {
    private Long id;
    private LocalDateTime fechaCanje;
    private String estado;
    private Integer puntosConsumidos;
    private RecompensaResponse recompensa;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CanjeResponse r = new CanjeResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder fechaCanje(LocalDateTime v) { r.fechaCanje = v; return this; }
        public Builder estado(String v) { r.estado = v; return this; }
        public Builder puntosConsumidos(Integer v) { r.puntosConsumidos = v; return this; }
        public Builder recompensa(RecompensaResponse v) { r.recompensa = v; return this; }
        public CanjeResponse build() { return r; }
    }

    public Long getId() { return id; }
    public LocalDateTime getFechaCanje() { return fechaCanje; }
    public String getEstado() { return estado; }
    public Integer getPuntosConsumidos() { return puntosConsumidos; }
    public RecompensaResponse getRecompensa() { return recompensa; }
}
