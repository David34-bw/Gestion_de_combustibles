package co.edu.unipiloto.fuelcontrol.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(name = "tipo_vehiculo", nullable = false)
    private String tipoVehiculo; // PARTICULAR, TAXI, MOTOCICLETA, CARGA

    @Column(name = "aplica_subsidio")
    private Boolean aplicaSubsidio = false;

    @Column(name = "numero_runt")
    private String numeroRunt;

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "solicitudes"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    protected void onCreate() { fechaRegistro = LocalDateTime.now(); }

    // Getters y Setters
    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }
    public String getPlaca()             { return placa; }
    public void setPlaca(String v)       { this.placa = v; }
    public String getTipoVehiculo()      { return tipoVehiculo; }
    public void setTipoVehiculo(String v){ this.tipoVehiculo = v; }
    public Boolean getAplicaSubsidio()   { return aplicaSubsidio; }
    public void setAplicaSubsidio(Boolean v) { this.aplicaSubsidio = v; }
    public String getNumeroRunt()        { return numeroRunt; }
    public void setNumeroRunt(String v)  { this.numeroRunt = v; }
    public String getMarca()             { return marca; }
    public void setMarca(String v)       { this.marca = v; }
    public String getModelo()            { return modelo; }
    public void setModelo(String v)      { this.modelo = v; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public Usuario getUsuario()          { return usuario; }
    public void setUsuario(Usuario u)    { this.usuario = u; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Vehiculo v = new Vehiculo();
        public Builder placa(String x)           { v.placa = x; return this; }
        public Builder tipoVehiculo(String x)    { v.tipoVehiculo = x; return this; }
        public Builder aplicaSubsidio(Boolean x) { v.aplicaSubsidio = x; return this; }
        public Builder numeroRunt(String x)      { v.numeroRunt = x; return this; }
        public Builder marca(String x)           { v.marca = x; return this; }
        public Builder modelo(String x)          { v.modelo = x; return this; }
        public Builder usuario(Usuario x)        { v.usuario = x; return this; }
        public Vehiculo build()                  { return v; }
    }
}
