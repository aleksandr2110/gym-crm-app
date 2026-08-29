package epam.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Schema(description = "Trainer profile details")
public class TrainerProfileDTO {

    @Schema(description = "Trainer ID", example = "2L")
    private Long id;

    @Schema(description = "Username", example = "John.Doe")
    private String username;

    @Schema(description = "First name", example = "Jane")
    private String firstName;

    @Schema(description = "Last name", example = "Smith")
    private String lastName;

    @Schema(description = "Training specialization", example = "JavaScript")
    private String specialization;

    @Schema(description = "Is active", example = "true")
    private Boolean isActive;

    @Schema(description = "List of trainees")
    private List<TraineeInfoDTO> trainees;
}
