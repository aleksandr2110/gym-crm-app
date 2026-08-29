package epam.domain.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Filter request for trainee trainings")
public class TraineeTrainingsRequestDTO {

    @NotBlank(message = "Trainee username is required")
    @Schema(description = "Trainee username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Emee.Lee")
    private String username;

    @Schema(description = "Period from date", example = "2026-07-01")
    private String periodFrom;

    @Schema(description = "Period to date", example = "2026-07-30")
    private String periodTo;

    @Schema(description = "Trainer name to filter by", example = "Josh.Gosling")
    private String trainerName;

    @Schema(description = "Training type to filter by", example = "Java")
    private String trainingType;
}
