package co.edu.unipiloto.fuelcontrol.dto.request;

import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    private String nombre;
    private String apellido;
    private String telefono;

    @Email(message = "Email inválido")
    @NotBlank(message = "El email es requerido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "Mínimo 6 caracteres")
    private String password;

    private String numeroDocumento;

    @NotNull(message = "El rol es requerido")
    private Rol rol;

    // Campos extra USUARIO
    private String placa;
    private String tipoVehiculo;
    private Boolean aplicaSubsidio;
    private String numeroRunt;

    // Getters y Setters
    public String getNombre()          { return nombre; }
    public void setNombre(String v)    { this.nombre = v; }
    public String getApellido()        { return apellido; }
    public void setApellido(String v)  { this.apellido = v; }
    public String getTelefono()        { return telefono; }
    public void setTelefono(String v)  { this.telefono = v; }
    public String getEmail()           { return email; }
    public void setEmail(String v)     { this.email = v; }
    public String getPassword()        { return password; }
    public void setPassword(String v)  { this.password = v; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String v) { this.numeroDocumento = v; }
    public Rol getRol()                { return rol; }
    public void setRol(Rol v)          { this.rol = v; }
    public String getPlaca()           { return placa; }
    public void setPlaca(String v)     { this.placa = v; }
    public String getTipoVehiculo()    { return tipoVehiculo; }
    public void setTipoVehiculo(String v) { this.tipoVehiculo = v; }
    public Boolean getAplicaSubsidio() { return aplicaSubsidio; }
    public void setAplicaSubsidio(Boolean v) { this.aplicaSubsidio = v; }
    public String getNumeroRunt()      { return numeroRunt; }
    public void setNumeroRunt(String v){ this.numeroRunt = v; }
}