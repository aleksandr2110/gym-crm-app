package epam.controller.rest;

import epam.application.FacadeGymCrmSystem;
import epam.controller.interfaces.TraineeController;
import epam.domain.dto.request.ChangePasswordRequestDTO;
import epam.domain.dto.request.TraineeRequestDTO;
import epam.domain.dto.request.UpdateTraineeRequestDTO;
import epam.domain.dto.request.UpdateTraineeTrainersRequestDTO;
import epam.domain.dto.response.RegistrationResponseDTO;
import epam.domain.dto.response.TraineeProfileDTO;
import epam.domain.dto.response.TrainerInfoDTO;
import epam.domain.entity.Role;
import epam.security.util.AuthenticatedUserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
public class TraineeControllerImpl implements TraineeController {

    private final FacadeGymCrmSystem facadeGymCrmSystem;
    private final AuthenticatedUserUtil authenticatedUserUtil;

    @Override
    public ResponseEntity<RegistrationResponseDTO> registerTrainee(TraineeRequestDTO request) {
        log.info("Register trainee request received: {} {}", request.getFirstName(), request.getLastName());

        RegistrationResponseDTO response = facadeGymCrmSystem.createTrainee(request);

        log.info("Trainee registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TraineeProfileDTO> getTraineeProfile(String username) {
        log.info("Get trainee profile request received for username: {}", username);

        TraineeProfileDTO profile = facadeGymCrmSystem.getTraineeByUsername(username);
        log.info("Trainee profile retrieved successfully for username: {}", username);
        return ResponseEntity.ok(profile);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTraineeProfile(String username) {
        log.info("Delete trainee profile request received for username: {}", username);

        facadeGymCrmSystem.deleteTrainee(username);

        log.info("Trainee profile deleted successfully for username: {}", username);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<TrainerInfoDTO>> getAvailableTrainers(String username) {
        log.info("Get available trainers request received for trainee: {}", username);

        List<TrainerInfoDTO> trainers = facadeGymCrmSystem.getAvailableTrainers(username);

        log.info("Found {} available trainers for trainee: {}", trainers.size(), username);
        return ResponseEntity.ok(trainers);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @authenticatedUserUtil.isProfileOwner(principal.id, authentication.name)")
    public ResponseEntity<TraineeProfileDTO> updateTraineeProfile(UpdateTraineeRequestDTO traineeRequestDTO,
                                                                  Long id) {
        log.info("Update trainee profile request received for username: {} {}", traineeRequestDTO.getUsername(), id);

        TraineeProfileDTO profile = facadeGymCrmSystem.updateTraineeProfile(traineeRequestDTO);

        log.info("Trainee profile updated successfully for username: {}", traineeRequestDTO.getUsername());
        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequestDTO request) {
        log.info("Change password request received for trainee: {}", request.getUsername());

        facadeGymCrmSystem.changeTraineePassword(request);

        log.info("Password changed successfully for trainee: {}", request.getUsername());
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateDeactivateTrainee(String username, Boolean isActive) {
        log.info("Activate/Deactivate trainee request received for username: {}, isActive: {}", username, isActive);

        facadeGymCrmSystem.activateDeactivateTrainee(username, isActive);

        log.info("Trainee status updated successfully for username: {}, new status: {}", username, isActive);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TrainerInfoDTO>> updateTrainersList(@Valid UpdateTraineeTrainersRequestDTO request) {
        log.info("Update trainers list request received for trainee: {}", request.getTraineeUsername());

        List<TrainerInfoDTO> trainers = facadeGymCrmSystem.updateTrainersList(request);

        log.info("Trainers list updated successfully for trainee: {}, trainers count: {}", request.getTraineeUsername(),
                trainers.size());
        return ResponseEntity.ok(trainers);
    }
}
