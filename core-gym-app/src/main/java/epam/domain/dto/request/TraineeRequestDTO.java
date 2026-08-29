package epam.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Trainee request for create and update")
public class TraineeRequestDTO {

    @NotBlank(message = "First name is required")
    @Schema(description = "First name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jeff")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Starmer")
    private String lastName;

    @Schema(description = "Date of birth", example = "1988-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Address", example = "56 Red St, City")
    private String address;
}
