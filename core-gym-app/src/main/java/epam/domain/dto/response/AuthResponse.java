package epam.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Schema(description = "Authentication response with JWT token")
public class AuthResponse {

    @Schema(description = "JWT access token")
    private String token;

    @Schema(description = "Token type")
    private String type = "Bearer";

    @Schema(description = "Username of authenticated user", example = "Auth.user.name")
    private String username;
}
