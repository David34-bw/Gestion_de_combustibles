package co.edu.unipiloto.fuelcontrol.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstacionUpdateRequest {

    private String nombre;
    private String direccion;
    private String ciudad;
    private String departamento;
}
