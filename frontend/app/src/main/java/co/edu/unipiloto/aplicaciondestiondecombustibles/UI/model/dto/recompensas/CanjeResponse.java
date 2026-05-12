package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.recompensas;

public class CanjeResponse {
    private Long id;
    private String fechaCanje;
    private String estado;
    private Integer puntosConsumidos;
    private RecompensaResponse recompensa;

    public Long getId() { return id; }
    public String getFechaCanje() { return fechaCanje; }
    public String getEstado() { return estado; }
    public Integer getPuntosConsumidos() { return puntosConsumidos; }
    public RecompensaResponse getRecompensa() { return recompensa; }
}
