package co.edu.unipiloto.fuelcontrol.dto.request;

import co.edu.unipiloto.fuelcontrol.domain.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    private String nombre;
    private String codigoAdmin;
    private String ciudad;
    private String departamento;
    private String direccion;
    private String codigoEntidad;
    private String cargo;
    private String dependencia;

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
    public String getCiudad()          { return ciudad; }
    public void setCiudad(String v)    { this.ciudad = v; }
    public String getDepartamento()    { return departamento; }
    public void setDepartamento(String v) { this.departamento = v; }
    public String getDireccion()       { return direccion; }
    public void setDireccion(String v) { this.direccion = v; }
    public String getCodigoEntidad()   { return codigoEntidad; }
    public void setCodigoEntidad(String v) { this.codigoEntidad = v; }
    public String getCargo()           { return cargo; }
    public void setCargo(String v)     { this.cargo = v; }
    public String getDependencia()     { return dependencia; }
    public void setDependencia(String v) { this.dependencia = v; }
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
