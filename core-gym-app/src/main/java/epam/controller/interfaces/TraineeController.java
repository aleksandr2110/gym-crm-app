package epam.controller.interfaces;

import epam.domain.dto.request.ChangePasswordRequestDTO;
import epam.domain.dto.request.TraineeRequestDTO;
import epam.domain.dto.request.UpdateTraineeRequestDTO;
import epam.domain.dto.request.UpdateTraineeTrainersRequestDTO;
import epam.domain.dto.response.RegistrationResponseDTO;
import epam.domain.dto.response.TraineeProfileDTO;
import epam.domain.dto.response.TrainerInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trainee Management", description = "Operations for trainee management")
public interface TraineeController {

    @PostMapping
    @Operation(summary = "Register new trainee", description = "Creates a new trainee profile and returns generated credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<RegistrationResponseDTO> registerTrainee(
            @Parameter(description = "Trainee registration data", required = true)
            @Valid @RequestBody TraineeRequestDTO request);

    @GetMapping("/{username}")
    @Operation(summary = "Get trainee profile", description = "Retrieves trainee profile information by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<TraineeProfileDTO> getTraineeProfile(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable("username") String username);

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee profile", description = "Deletes a trainee profile by username (hard delete with cascade)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<Void> deleteTraineeProfile(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable("username") String username);

    @GetMapping("/{username}/available-trainers")
    @Operation(summary = "Get available trainers", description = "Retrieves active trainers not assigned to the trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available trainers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<List<TrainerInfoDTO>> getAvailableTrainers(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable("username") String username);


    @PutMapping("/{id}")
    @Operation(summary = "Update trainee profile", description = "Updates trainee profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<TraineeProfileDTO> updateTraineeProfile(
            @Parameter(description = "Trainee update data", required = true)
            @Valid @RequestBody UpdateTraineeRequestDTO request,
            @Parameter(description = "Trainee id", required = true)
            @PathVariable("id") Long id);

    @PutMapping("/change-password")
    @Operation(summary = "Change trainee password", description = "Changes the password for a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Invalid old password")
    })
    ResponseEntity<Void> changePassword(
            @Parameter(description = "Password change request", required = true)
            @Valid @RequestBody ChangePasswordRequestDTO request);

    @PatchMapping("/activation")
    @Operation(summary = "Activate/Deactivate trainee", description = "Activates or deactivates a trainee profile (non-idempotent operation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<Void> activateDeactivateTrainee(
            @Parameter(description = "Trainee username", required = true)
            @RequestParam("username") String username,
            @Parameter(description = "Active status", required = true)
            @RequestParam("isActive") Boolean isActive);

    @PutMapping("/trainers")
    @Operation(summary = "Update trainee's trainer list", description = "Updates the list of trainers assigned to a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers list updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<List<TrainerInfoDTO>> updateTrainersList(
            @Parameter(description = "Update trainers list request", required = true)
            @Valid @RequestBody UpdateTraineeTrainersRequestDTO request);
}
