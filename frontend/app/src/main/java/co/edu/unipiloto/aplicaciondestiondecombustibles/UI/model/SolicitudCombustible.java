package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model;

public class SolicitudCombustible {
    private Long id;
    private String tipoCombustible;   // "GASOLINA" o "DIESEL"
    private Double cantidad;
    private String estado;            // "PENDIENTE", "APROBADA", "RECHAZADA", "ENTREGADA"
    private String observaciones;
    private String motivoRechazo;
    private String fechaSolicitud;
    private Long usuarioId;
    private String usuarioNombre;
    private Long estacionId;
    private String estacionNombre;
}