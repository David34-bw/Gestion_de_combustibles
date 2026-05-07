package co.edu.unipiloto.fuelcontrol.dto.response;

import java.time.LocalDateTime;

public class VentaResponse {
    private Long id;
    private String tipoCombustible;
    private Double cantidad;
    private LocalDateTime fechaVenta;
    private String observaciones;
    private Long estacionId;
    private String estacionNombre;
    private Boolean alertaStockBajo;
    private Long usuarioId;
    private String usuarioNombre;
    private String placaVehiculo;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final VentaResponse r = new VentaResponse();
        public Builder id(Long v)                   { r.id = v; return this; }
        public Builder tipoCombustible(String v)    { r.tipoCombustible = v; return this; }
        public Builder cantidad(Double v)           { r.cantidad = v; return this; }
        public Builder fechaVenta(LocalDateTime v)  { r.fechaVenta = v; return this; }
        public Builder observaciones(String v)      { r.observaciones = v; return this; }
        public Builder estacionId(Long v)           { r.estacionId = v; return this; }
        public Builder estacionNombre(String v)     { r.estacionNombre = v; return this; }
        public Builder alertaStockBajo(Boolean v)   { r.alertaStockBajo = v; return this; }
        public VentaResponse build()                { return r; }
        public Builder usuarioId(Long v)       { r.usuarioId = v; return this; }
        public Builder usuarioNombre(String v) { r.usuarioNombre = v; return this; }
        public Builder placaVehiculo(String v) { r.placaVehiculo = v; return this; }
    }

    public Long getId()                    { return id; }
    public String getTipoCombustible()     { return tipoCombustible; }
    public Double getCantidad()            { return cantidad; }
    public LocalDateTime getFechaVenta()   { return fechaVenta; }
    public String getObservaciones()       { return observaciones; }
    public Long getEstacionId()            { return estacionId; }
    public String getEstacionNombre()      { return estacionNombre; }
    public Boolean getAlertaStockBajo()    { return alertaStockBajo; }
    public Long getUsuarioId()         { return usuarioId; }
    public String getUsuarioNombre()   { return usuarioNombre; }
    public String getPlacaVehiculo()   { return placaVehiculo; }
}