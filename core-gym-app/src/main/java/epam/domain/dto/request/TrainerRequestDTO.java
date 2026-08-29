package epam.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Trainer request for create and update")
public class TrainerRequestDTO {

    @NotBlank(message = "First name is required")
    @Schema(description = "First name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jasmin")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Benedict")
    private String lastName;

    @NotBlank(message = "Specialization is required")
    @Schema(description = "Training specialization", requiredMode = Schema.RequiredMode.REQUIRED, example = "Java")
    private String specialization;
}
