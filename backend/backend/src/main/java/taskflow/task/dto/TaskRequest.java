package taskflow.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "El título es obligatorio") 
        @Size(max = 120, message = "Máximo 120 caracteres") 
        String title,

        @Size(max = 500, message = "Máximo 500 caracteres") 
        String description) {
}