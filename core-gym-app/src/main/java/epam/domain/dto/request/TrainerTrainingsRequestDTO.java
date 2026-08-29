package epam.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Filter request for trainer trainings")
public class TrainerTrainingsRequestDTO {

    @NotBlank(message = "Trainer username is required")
    @Schema(description = "Trainer username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jonny.Smith")
    private String username;

    @Schema(description = "Period from date", example = "2026-07-10T15:30:00")
    private String periodFrom;

    @Schema(description = "Period to date", example = "2026-07-30T15:30:00")
    private String periodTo;

    @Schema(description = "Trainee name to filter by", example = "Jeff.Smith")
    private String traineeName;
}
