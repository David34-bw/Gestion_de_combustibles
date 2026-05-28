package co.edu.unipiloto.fuelcontrol.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstacionCreateRequest {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "La direccion es requerida")
    private String direccion;

    private String ciudad;
    private String departamento;
    private String nit;
}
