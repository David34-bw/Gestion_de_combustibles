package co.edu.unipiloto.fuelcontrol.domain.dto.request;


public record UsuarioUpdateDTO(
    String nombre,
    String email,
    String numeroDocumento,
    String password // Opcional, solo si se envía
    ) {}

