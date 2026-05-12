package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.requests;

public class ResolucionRequest {
    private String estado;         // "APROBADA" o "RECHAZADA"
    private String motivoRechazo;
    private Long distribuidorId;
    private Long estacionId;
}