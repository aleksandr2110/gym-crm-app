package epam.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Trainer update request")
public class UpdateTrainerRequestDTO {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Adam.Lee")
    private String username;

    @NotBlank(message = "First name is required")
    @Schema(description = "First name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Adam")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Lee")
    private String lastName;

    @NotBlank(message = "Specialization is required")
    @Schema(description = "Specialization (read only)", requiredMode = Schema.RequiredMode.REQUIRED, example = "Phyton")
    private String specialization;

    @NotNull(message = "IsActive is required")
    @Schema(description = "Active status", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean isActive;
}
