package epam.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Schema(description = "Training trainer basic information")
public class TrainingTrainerDTO {

    @Schema(description = "Trainee username", example = "Ricci.Deep")
    private String traineeName;
    @Schema(description = "Training specialization name", example = "Java")
    private String trainingType;
    @Schema(description = "Training name", example = "Java learning")
    private String trainingName;
    @Schema(description = "Training date", example = "2026-07-22T07:14:00")
    private LocalDateTime trainingDate;
    @Schema(description = "Training duration", example = "60")
    private Integer trainingDuration;
}
