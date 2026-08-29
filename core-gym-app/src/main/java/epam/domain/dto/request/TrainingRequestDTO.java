package epam.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Training creation request")
public class TrainingRequestDTO {

    @NotBlank(message = "Trainer username is required")
    @Schema(description = "Trainer username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jonny.Dep")
    private String trainerUsername;

    @NotBlank(message = "Trainee username is required")
    @Schema(description = "Trainee username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Kristina.Genri")
    private String traineeUsername;

    @NotBlank(message = "Training name is required")
    @Schema(description = "Training name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Learning java")
    private String trainingName;

    @Schema(description = "Training type", example = "java")
    private String trainingType;

    @NotNull(message = "Training date is required")
    @Schema(description = "Training date", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-21-13-20-00")
    private String trainingDate;

    @NotNull(message = "Training duration is required")
    @Schema(description = "Training duration in minutes", requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
    private Integer trainingDuration;
}
