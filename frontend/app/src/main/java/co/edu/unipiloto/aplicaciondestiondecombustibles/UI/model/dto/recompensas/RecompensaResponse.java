package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.recompensas;

public class RecompensaResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer costoPuntos;
    private Integer porcentajeDescuento;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Integer getCostoPuntos() { return costoPuntos; }
    public Integer getPorcentajeDescuento() { return porcentajeDescuento; }
}
