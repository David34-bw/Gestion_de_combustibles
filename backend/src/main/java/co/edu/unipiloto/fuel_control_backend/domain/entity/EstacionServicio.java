package co.edu.unipiloto.fuel_control_backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estaciones_servicio")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EstacionServicio extends Usuario {

    @Column(nullable = false, unique = true)
    private String nit;

    @Column(nullable = false)
    private String nombreComercial;

    @Column(nullable = false, unique = true)
    private String codigoSicom;

    @Column(nullable = false)
    private String licenciaOperacion;

    private String direccion;
    private String ciudad;
    private String departamento;
    private boolean verificadaMinEnergia = false;
}