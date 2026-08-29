package epam.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@Schema(description = "TrainingTypeDto")
@AllArgsConstructor
@NoArgsConstructor
public class TrainingTypeDTO {

    @Schema(description = "Training type Id", example = "2L")
    private Long id;

    @Schema(description = "Training type", example = "TypeScript")
    private String trainingTypeName;
}
