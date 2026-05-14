package co.edu.unipiloto.fuelcontrol.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_particulares")
public class UsuarioParticular {

    @Id
    private Long id;

    @Column(name = "puntos_acumulados", nullable = false, columnDefinition = "integer default 0")
    private Integer puntosAcumulados = 0;

    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "solicitudes"})
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    protected void onCreate() { fechaRegistro = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getPuntosAcumulados() { return puntosAcumulados; }
    public void setPuntosAcumulados(Integer puntosAcumulados) { this.puntosAcumulados = puntosAcumulados; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
