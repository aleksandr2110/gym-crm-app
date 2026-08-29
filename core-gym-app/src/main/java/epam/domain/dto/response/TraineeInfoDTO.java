package epam.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Schema(description = "Trainee basic information")
public class TraineeInfoDTO {

    @Schema(description = "Trainee ID", example = "1L")
    private Long id;

    @Schema(description = "Trainee username", example = "Jonny.Tim")
    private String username;

    @Schema(description = "Trainee first name", example = "Jonny")
    private String firstName;

    @Schema(description = "Trainee last name", example = "Tim")
    private String lastName;
}
