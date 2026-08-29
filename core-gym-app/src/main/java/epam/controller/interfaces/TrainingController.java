package epam.controller.interfaces;

import epam.domain.dto.request.TraineeTrainingsRequestDTO;
import epam.domain.dto.request.TrainerTrainingsRequestDTO;
import epam.domain.dto.request.TrainingRequestDTO;
import epam.domain.dto.response.TrainingTraineeDTO;
import epam.domain.dto.response.TrainingTrainerDTO;
import epam.domain.dto.response.TrainingTypeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Training Management", description = "Operations for training management")
public interface TrainingController {

    @GetMapping("/types")
    @Operation(summary = "Get Training types", description = "Retrieves list of all training types")
    @ApiResponse(responseCode = "200", description = "Types retrieved successfully")
    ResponseEntity<List<TrainingTypeDTO>> getTrainingTypes();

    @PostMapping
    @Operation(summary = "Add training", description = "Creates a new training session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    ResponseEntity<Void> addTraining(
            @Parameter(description = "Training creation data", required = true)
            @Valid @RequestBody TrainingRequestDTO request);

    @GetMapping("/trainee")
    @Operation(summary = "Get trainee trainings", description = "Retrieves list of trainings for a trainee with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<List<TrainingTraineeDTO>> getTraineeTrainings(
            @Parameter(description = "Filter criteria for trainee trainings", required = true)
            @Valid @RequestBody TraineeTrainingsRequestDTO filterRequest);

    @GetMapping("/trainer")
    @Operation(summary = "Get trainer trainings", description = "Retrieves list of trainings for a trainer with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    ResponseEntity<List<TrainingTrainerDTO>> getTrainerTrainings(
            @Parameter(description = "Filter criteria for trainer trainings", required = true)
            @Valid @RequestBody TrainerTrainingsRequestDTO filterRequest);
}
