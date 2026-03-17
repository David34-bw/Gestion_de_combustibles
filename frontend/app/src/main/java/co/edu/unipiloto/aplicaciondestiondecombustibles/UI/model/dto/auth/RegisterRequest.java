package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.dto.auth;

public class RegisterRequest {

    // Campos comunes a todos los roles
    private String email;
    private String password;
    private String rol;

    // Campos empresa (estación, distribuidor, regulador)
    private String nombre;
    private String numeroDocumento; // NIT

    // Campos exclusivos usuario particular
    private String placa;
    private String tipoVehiculo;
    private Boolean aplicaSubsidio;
    private String numeroRunt;

    // ── Constructor usuario particular (desde Dashboard)
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

    public String getNombre()           { return nombre; }
    public String getEmail()            { return email; }
    public String getPassword()         { return password; }
    public String getNumeroDocumento()  { return numeroDocumento; }
    public String getRol()              { return rol; }
    public String getPlaca()            { return placa; }
    public String getTipoVehiculo()     { return tipoVehiculo; }
    public Boolean getAplicaSubsidio()  { return aplicaSubsidio; }
    public String getNumeroRunt()       { return numeroRunt; }
}