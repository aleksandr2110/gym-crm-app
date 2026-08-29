package epam.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Schema(description = "Trainee profile details")
public class TraineeProfileDTO {

    @Schema(description = "Trainee ID", example = "1L")
    private Long id;

    @Schema(description = "Username", example = "John.Tee")
    private String username;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Tee")
    private String lastName;

    @Schema(description = "Date of birth", example = "1988-07-17")
    private LocalDate dateOfBirth;

    @Schema(description = "Address", example = "88 Red St, City")
    private String address;

    @Schema(description = "Is active", example = "true")
    private Boolean isActive;

    @Schema(description = "List of assigned trainers")
    private List<TrainerInfoDTO> trainers;
}
