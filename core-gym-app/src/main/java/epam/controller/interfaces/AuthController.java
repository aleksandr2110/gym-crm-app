package epam.controller.interfaces;

import epam.domain.dto.request.LoginRequest;
import epam.domain.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Authentication", description = "Authentication endpoints for login")
public interface AuthController {

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token", security = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    ResponseEntity<AuthResponse> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequest request);
}
