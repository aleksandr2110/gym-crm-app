package epam.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Trainee update request")
public class UpdateTraineeTrainersRequestDTO {

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @NotNull(message = "Trainers list is required")
    private List<String> trainerUsernames;
}
