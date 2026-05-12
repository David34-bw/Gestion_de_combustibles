package co.edu.unipiloto.fuelcontrol.dto.response;

public class RecompensaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer costoPuntos;
    private Integer porcentajeDescuento;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final RecompensaResponse r = new RecompensaResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder nombre(String v) { r.nombre = v; return this; }
        public Builder descripcion(String v) { r.descripcion = v; return this; }
        public Builder costoPuntos(Integer v) { r.costoPuntos = v; return this; }
        public Builder porcentajeDescuento(Integer v) { r.porcentajeDescuento = v; return this; }
        public RecompensaResponse build() { return r; }
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Integer getCostoPuntos() { return costoPuntos; }
    public Integer getPorcentajeDescuento() { return porcentajeDescuento; }
}
