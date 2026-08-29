package epam.domain.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Login credentials")
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username", requiredMode = Schema.RequiredMode.REQUIRED, example = "John.Doe")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password", requiredMode = Schema.RequiredMode.REQUIRED, example = "password123")
    private String password;
}
