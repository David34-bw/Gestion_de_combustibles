package co.edu.unipiloto.fuelcontrol.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "canjes_recompensa")
public class CanjeRecompensa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recompensa_id", nullable = false)
    private Recompensa recompensa;

    @Column(name = "fecha_canje", nullable = false)
    private LocalDateTime fechaCanje;

    @Column(nullable = false)
    private String estado;

    @PrePersist
    protected void onCreate() {
        fechaCanje = LocalDateTime.now();
        if (estado == null) {
            estado = "ACTIVO";
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Recompensa getRecompensa() { return recompensa; }
    public void setRecompensa(Recompensa recompensa) { this.recompensa = recompensa; }
    public LocalDateTime getFechaCanje() { return fechaCanje; }
    public void setFechaCanje(LocalDateTime fechaCanje) { this.fechaCanje = fechaCanje; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
