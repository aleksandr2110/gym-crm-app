package epam.domain.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Schema(description = "Registration response with generated credentials")
public class RegistrationResponseDTO {

    @Schema(description = "Generated username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jeff.Starmer")
    private String username;

    @Schema(description = "Generated password", requiredMode = Schema.RequiredMode.REQUIRED, example = "abcd890wxyz")
    private String password;
}
