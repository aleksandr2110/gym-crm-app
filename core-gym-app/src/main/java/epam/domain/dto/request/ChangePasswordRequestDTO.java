package epam.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Change password request")
public class ChangePasswordRequestDTO {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jastin.Trudo")
    private String username;

    @NotBlank(message = "Old password is required")
    @Schema(description = "Old password", requiredMode = Schema.RequiredMode.REQUIRED, example = "oldPassword434")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Schema(description = "New password", requiredMode = Schema.RequiredMode.REQUIRED, example = "newPassword456")
    private String newPassword;
}
