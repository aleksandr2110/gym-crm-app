package epam.controller.rest;

import epam.controller.interfaces.AuthController;
import epam.domain.dto.request.LoginRequest;
import epam.domain.dto.response.AuthResponse;
import epam.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        log.info("Login request received for username: {}", request.getUsername());

        AuthResponse response = authService.login(request);

        log.info("Login successful for username: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }
}
