package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth;

public class RegisterRequest {

    // Campos comunes a todos los roles
    private String email;
    private String password;
    private String rol;

    // Campos empresa (estación, distribuidor, regulador)
    private String nombre;
    private String numeroDocumento; // NIT
    private String ciudad;
    private String departamento;
    private String direccion;
    private String codigoEntidad;
    private String cargo;
    private String dependencia;


    private String codigoAdmin;

    // ── Constructor empresa (estación, distribuidor, regulador)
    public RegisterRequest(String nombre, String email, String password,
                           String numeroDocumento, String rol) {
        this.nombre          = nombre;
        this.email           = email;
        this.password        = password;
        this.numeroDocumento = numeroDocumento;
        this.rol             = rol;
    }

    // ── Constructor simple — solo para USUARIO desde RegisterActivity
    public RegisterRequest(String email, String password, String rol) {
        this.email    = email;
        this.password = password;
        this.rol      = rol;
    }

    public RegisterRequest(String email, String password, String rol, String numeroDocumento) {
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombre()           { return nombre; }

    public String getNumeroDocumento()  { return numeroDocumento; }
    public String getRol()              { return rol; }
    public String getCiudad()           { return ciudad; }
    public String getDepartamento()     { return departamento; }
    public String getDireccion()        { return direccion; }
    public String getCodigoEntidad()    { return codigoEntidad; }
    public String getCargo()            { return cargo; }
    public String getDependencia()      { return dependencia; }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }


    public void setCodigoAdmin(String codigoAdmin) {
        this.codigoAdmin = codigoAdmin;
    }

    public String getCodigoAdmin() { return codigoAdmin; }
}
