package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model;

public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String numeroDocumento;
    private String telefono;
    private String rol;
    private String placa;
    private String tipoVehiculo;
    private Boolean aplicaSubsidio;
    private String numeroRunt;

    public RegisterRequest(String email, String password, String numeroDocumento,
                           String rol, String placa, String tipoVehiculo,
                           Boolean aplicaSubsidio, String numeroRunt) {
        this.email           = email;
        this.password        = password;
        this.numeroDocumento = numeroDocumento;
        this.rol             = rol;
        this.placa           = placa;
        this.tipoVehiculo    = tipoVehiculo;
        this.aplicaSubsidio  = aplicaSubsidio;
        this.numeroRunt      = numeroRunt;
    }

    // Constructor general para otros roles
    public RegisterRequest(String nombre, String apellido, String email,
                           String password, String numeroDocumento, String rol) {
        this.nombre          = nombre;
        this.apellido        = apellido;
        this.email           = email;
        this.password        = password;
        this.numeroDocumento = numeroDocumento;
        this.rol             = rol;
    }

    public String getNombre()             { return nombre; }
    public String getApellido()           { return apellido; }
    public String getEmail()              { return email; }
    public String getPassword()           { return password; }
    public String getNumeroDocumento()    { return numeroDocumento; }
    public String getTelefono()           { return telefono; }
    public String getRol()                { return rol; }
    public String getPlaca()              { return placa; }
    public String getTipoVehiculo()       { return tipoVehiculo; }
    public Boolean getAplicaSubsidio()    { return aplicaSubsidio; }
    public String getNumeroRunt()         { return numeroRunt; }
}