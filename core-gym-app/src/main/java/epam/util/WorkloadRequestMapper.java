package epam.util;

import epam.domain.dto.request.WorkloadRequest;
import epam.domain.entity.Training;
import org.springframework.stereotype.Component;

@Component
public class WorkloadRequestMapper {

    public static WorkloadRequest fromTraining(Training training, String action) {
        return WorkloadRequest.builder()
                .username(training.getTrainer().getUsername())
                .firstName(training.getTrainer().getFirstName())
                .lastName(training.getTrainer().getLastName())
                .isActive(training.getTrainer().isActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(action)
                .build();
    }
}
