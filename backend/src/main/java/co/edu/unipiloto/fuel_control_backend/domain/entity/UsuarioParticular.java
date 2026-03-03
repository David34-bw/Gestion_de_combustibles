package co.edu.unipiloto.fuel_control_backend.domain.entity;

import co.edu.unipiloto.fuel_control_backend.domain.enums.TipoVehiculo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios_particulares")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UsuarioParticular extends Usuario {

    @Column(nullable = false)
    private String cedula;

    @Column(nullable = false, length = 6)
    private String placaVehiculo;

    @Enumerated(EnumType.STRING)
    private TipoVehiculo tipoVehiculo;

    private boolean tieneSubsidio = false;
    private String numeroRunt;
    private boolean subsidioVerificado = false;
}