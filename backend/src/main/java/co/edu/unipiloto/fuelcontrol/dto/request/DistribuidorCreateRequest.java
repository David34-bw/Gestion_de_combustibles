package co.edu.unipiloto.fuelcontrol.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistribuidorCreateRequest {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El nit es requerido")
    private String nit;

    private String ciudad;
    private String departamento;
}
