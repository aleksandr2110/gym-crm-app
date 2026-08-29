package epam.controller.rest;

import epam.application.FacadeGymCrmSystem;
import epam.controller.interfaces.TrainerController;
import epam.domain.dto.request.ChangePasswordRequestDTO;
import epam.domain.dto.request.TrainerRequestDTO;
import epam.domain.dto.request.UpdateTrainerRequestDTO;
import epam.domain.dto.response.RegistrationResponseDTO;
import epam.domain.dto.response.TrainerProfileDTO;
import epam.security.util.AuthenticatedUserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerControllerImpl implements TrainerController {

    private final FacadeGymCrmSystem facadeGymCrmSystem;
    private final AuthenticatedUserUtil authenticatedUserUtil;

    @Override
    public ResponseEntity<RegistrationResponseDTO> registerTrainer(@Valid TrainerRequestDTO request) {
        log.info("Register trainer request received: {} {}", request.getFirstName(), request.getLastName());

        RegistrationResponseDTO response = facadeGymCrmSystem.createTrainer(request);

        log.info("Trainer registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TrainerProfileDTO> getTrainerProfile(String username) {
        log.info("Get trainer profile request received for username: {}", username);

        var profile = facadeGymCrmSystem.getTrainerByUsername(username);

        log.info("Trainer profile retrieved successfully for username: {}", username);
        return ResponseEntity.ok(profile);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @authenticatedUserUtil.isProfileOwner(principal.id, authentication.name)")
    public ResponseEntity<TrainerProfileDTO> updateTrainerProfile(UpdateTrainerRequestDTO request,
                                                                  Long id) {
        log.info("Update trainer profile request received for username: {}", request.getUsername());

        var profile = facadeGymCrmSystem.updateTrainerProfile(request);

        log.info("Trainer profile updated successfully for username: {}", request.getUsername());
        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<Void> changePassword(@Valid ChangePasswordRequestDTO request) {
        log.info("Change password request received for trainer: {}", request.getUsername());

        facadeGymCrmSystem.changeTrainerPassword(request);

        log.info("Password changed successfully for trainer: {}", request.getUsername());
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateDeactivateTrainer(String username, Boolean isActive) {
        log.info("Activate/Deactivate trainer request received for username: {}, isActive: {}", username, isActive);

        facadeGymCrmSystem.activateDeactivateTrainer(username, isActive);

        log.info("Trainer status updated successfully for username: {}, new status: {}", username, isActive);
        return ResponseEntity.ok().build();
    }
}
