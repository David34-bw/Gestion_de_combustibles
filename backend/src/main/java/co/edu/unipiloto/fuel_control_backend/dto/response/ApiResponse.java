package co.edu.unipiloto.fuel_control_backend.dto.response;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
}