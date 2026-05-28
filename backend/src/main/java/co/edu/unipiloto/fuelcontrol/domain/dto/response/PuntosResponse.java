package co.edu.unipiloto.fuelcontrol.domain.dto.response;


public class PuntosResponse {
    private Long usuarioId;
    private Integer puntosAcumulados;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PuntosResponse r = new PuntosResponse();
        public Builder usuarioId(Long v) { r.usuarioId = v; return this; }
        public Builder puntosAcumulados(Integer v) { r.puntosAcumulados = v; return this; }
        public PuntosResponse build() { return r; }
    }

    public Long getUsuarioId() { return usuarioId; }
    public Integer getPuntosAcumulados() { return puntosAcumulados; }
}
