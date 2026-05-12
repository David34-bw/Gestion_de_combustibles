package co.edu.unipiloto.fuelcontrol.dto.request;

import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    private String nombre;
    private String codigoAdmin;

    @Email(message = "Email inválido")
    @NotBlank(message = "El email es requerido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "Mínimo 6 caracteres")
    private String password;

    private String numeroDocumento;

    @NotNull(message = "El rol es requerido")
    private Rol rol;

    public String getNombre()          { return nombre; }
    public void setNombre(String v)    { this.nombre = v; }
    public String getEmail()           { return email; }
    public void setEmail(String v)     { this.email = v; }
    public String getPassword()        { return password; }
    public void setPassword(String v)  { this.password = v; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String v) { this.numeroDocumento = v; }
    public Rol getRol()                { return rol; }
    public void setRol(Rol v)          { this.rol = v; }
    public String getCodigoAdmin()     { return codigoAdmin; }
    public void setCodigoAdmin(String v) { this.codigoAdmin = v; }
}