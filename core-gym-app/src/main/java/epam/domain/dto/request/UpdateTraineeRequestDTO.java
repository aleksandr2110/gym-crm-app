package epam.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Trainee update request")
public class UpdateTraineeRequestDTO {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jonatan.Trudo")
    private String username;

    @NotBlank(message = "First name is required")
    @Schema(description = "First name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jonatan")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Trudo")
    private String lastName;

    @Schema(description = "Date of birth", example = "1987-11-15")
    private String dateOfBirth;

    @Schema(description = "Address", example = "65 White road St, City")
    private String address;

    @NotNull(message = "IsActive is required")
    @Schema(description = "Active status", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean isActive;
}
