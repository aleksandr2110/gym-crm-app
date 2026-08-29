package epam.controller.rest;

import epam.application.FacadeGymCrmSystem;
import epam.controller.interfaces.TrainingController;
import epam.domain.dto.request.TraineeTrainingsRequestDTO;
import epam.domain.dto.request.TrainerTrainingsRequestDTO;
import epam.domain.dto.request.TrainingRequestDTO;
import epam.domain.dto.response.TrainingTraineeDTO;
import epam.domain.dto.response.TrainingTrainerDTO;
import epam.domain.dto.response.TrainingTypeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingControllerImpl implements TrainingController {

    private final FacadeGymCrmSystem facadeGymCrmSystem;

    @Override
    public ResponseEntity<List<TrainingTypeDTO>> getTrainingTypes() {
        log.info("Get training types request received for username");

        List<TrainingTypeDTO> types = facadeGymCrmSystem.getTrainingTypes();

        log.info("Training types retrieved successfully!");
        return ResponseEntity.ok(types);
    }

    @Override
    public ResponseEntity<Void> addTraining(TrainingRequestDTO request) {
        log.info("Add training request received: trainee={}, trainer={}, name={}",
                request.getTraineeUsername(), request.getTrainerUsername(), request.getTrainingName());

        facadeGymCrmSystem.createTraining(request);

        log.info("Training created successfully: trainee={}, trainer={}, name={}",
                request.getTraineeUsername(), request.getTrainerUsername(), request.getTrainingName());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TrainingTraineeDTO>> getTraineeTrainings(TraineeTrainingsRequestDTO filterRequest) {
        log.info("Get trainee trainings request received for username: {}, filters: from={}, to={}, trainer={}, type={}",
                filterRequest.getUsername(), filterRequest.getPeriodFrom(), filterRequest.getPeriodTo(),
                filterRequest.getTrainerName(), filterRequest.getTrainingType());

        List<TrainingTraineeDTO> trainings = facadeGymCrmSystem.getTraineeTrainings(filterRequest);

        log.info("Trainee trainings retrieved successfully for username: {}, count: {}",
                filterRequest.getUsername(), trainings.size());

        return ResponseEntity.ok(trainings);
    }

    @Override
    public ResponseEntity<List<TrainingTrainerDTO>> getTrainerTrainings(TrainerTrainingsRequestDTO filterRequest) {
        log.info("Get trainer trainings request received for username: {}, filters: from={}, to={}, trainee={}",
                filterRequest.getUsername(), filterRequest.getPeriodFrom(), filterRequest.getPeriodTo(),
                filterRequest.getTraineeName());

        List<TrainingTrainerDTO> trainings = facadeGymCrmSystem.getTrainerTrainings(filterRequest);

        log.info("Trainer trainings retrieved successfully for username: {}, count: {}",
                filterRequest.getUsername(), trainings.size());
        return ResponseEntity.ok(trainings);
    }
}
