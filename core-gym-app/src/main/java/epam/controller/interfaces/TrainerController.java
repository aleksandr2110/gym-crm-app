package epam.controller.interfaces;


import epam.domain.dto.request.ChangePasswordRequestDTO;
import epam.domain.dto.request.TrainerRequestDTO;
import epam.domain.dto.request.UpdateTrainerRequestDTO;
import epam.domain.dto.response.RegistrationResponseDTO;
import epam.domain.dto.response.TrainerProfileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trainer Management", description = "Operations for trainer management")
public interface TrainerController {

    @PostMapping
    @Operation(summary = "Register new trainer", description = "Creates a new trainer profile and returns generated credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<RegistrationResponseDTO> registerTrainer(
            @Parameter(description = "Trainer registration data", required = true)
            @Valid @RequestBody TrainerRequestDTO request);

    @GetMapping("/{username}")
    @Operation(summary = "Get trainer profile", description = "Retrieves trainer profile information by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    ResponseEntity<TrainerProfileDTO> getTrainerProfile(
            @Parameter(description = "Trainer username", required = true)
            @PathVariable("username") String username);

    @PutMapping("/{id}")
    @Operation(summary = "Update trainer profile", description = "Updates trainer profile information (specialization is read-only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<TrainerProfileDTO> updateTrainerProfile(
            @Parameter(description = "Trainer update data", required = true)
            @Valid @RequestBody UpdateTrainerRequestDTO request, @PathVariable("id") Long id);

    @PutMapping("/change-password")
    @Operation(summary = "Change trainer password", description = "Changes the password for a trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Invalid old password")
    })
    ResponseEntity<Void> changePassword(
            @Parameter(description = "Password change request", required = true)
            @Valid @RequestBody ChangePasswordRequestDTO request);

    @PatchMapping("/activation")
    @Operation(summary = "Activate/Deactivate trainer", description = "Activates or deactivates a trainer profile (non-idempotent operation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    ResponseEntity<Void> activateDeactivateTrainer(
            @Parameter(description = "Trainer username", required = true)
            @RequestParam("username") String username,
            @Parameter(description = "Active status", required = true)
            @RequestParam("isActive") Boolean isActive);
}