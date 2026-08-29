package epam.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Schema(description = "Trainer basic information")
public class TrainerInfoDTO {

    @Schema(description = "Trainer ID", example = "2L")
    private Long id;

    @Schema(description = "Trainer username", example = "Jane.Dark")
    private String username;

    @Schema(description = "Trainer first name", example = "Jane")
    private String firstName;

    @Schema(description = "Trainer last name", example = "Dark")
    private String lastName;

    @Schema(description = "Training specialization", example = "JAVA")
    private String specialization;
}
