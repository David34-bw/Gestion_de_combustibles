package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.model.entity;

public class Usuario {
    private Long id;
    private String nombre, apellido, email, telefono, rol,numeroDocumento;
    private Boolean activo;
    private Integer puntosAcumulados;

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Integer getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public void setPuntosAcumulados(Integer puntosAcumulados) {
        this.puntosAcumulados = puntosAcumulados;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
