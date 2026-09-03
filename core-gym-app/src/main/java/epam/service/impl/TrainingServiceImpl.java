package epam.service.impl;

import epam.annotation.ExecutionTime;
import epam.domain.dto.request.WorkloadRequest;
import epam.domain.entity.Training;
import epam.monitoring.metrics.TrainingMetrics;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.repository.TrainingRepository;
import epam.repository.TrainingTypeRepository;
import epam.service.TrainingService;
import epam.service.WorkloadService;
import epam.util.WorkloadRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMetrics trainingMetrics;
    private final WorkloadService workloadService;

    public TrainingServiceImpl(TrainingRepository trainingRepository,
                               TraineeRepository traineeRepository,
                               TrainerRepository trainerRepository,
                               TrainingTypeRepository trainingTypeRepository,
                               TrainingMetrics trainingMetrics, WorkloadService workloadService) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingMetrics = trainingMetrics;
        this.workloadService =  workloadService;
    }

    @ExecutionTime
    @Transactional
    @Override
    public void save(Training trainingRequest) {
        var training = getTraining(trainingRequest);

        training.setTrainingName(trainingRequest.getTrainingName());
        training.setTrainingDate(trainingRequest.getTrainingDate());
        training.setTrainingDuration(trainingRequest.getTrainingDuration());

        trainingRepository.save(training);

        trainingMetrics.incrementTrainingCreated();
        trainingMetrics.incrementActiveTrainings();

        workloadService.updateWorkload(WorkloadRequestMapper.fromTraining(training, "add"));
    }

    @Override
    public Training findTrainingById(Long id) {
        return trainingRepository.findTrainingById(id);
    }

    @Override
    public List<Training> getTrainingByTrainingTypeName(String trainingTypeName) {
        return trainingRepository.getTrainingByTrainingTypeName(trainingTypeName);
    }

    @Transactional
    @Override
    public List<Training> selectTraineeTrainings(String username, LocalDateTime fromDate, LocalDateTime toDate,
                                                 String trainingType) {
        List<Training> trainings = trainingRepository.findTraineeTrainingsByUserNameAndDate(username,
                fromDate, toDate, trainingType);

        return trainings;
    }

    @Transactional
    @Override
    public List<Training> selectTrainerTrainings(String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate) {

        return trainingRepository.findTrainerTrainingsByUserNameAndDate(trainerUsername,
                fromDate, toDate);

    }

    private Training getTraining(Training trainingRequest) {
        var training = new Training();
        var trainee = traineeRepository.findByUsername(trainingRequest.getTrainee().getUsername());
        if (trainee.isEmpty()) {
            throw new IllegalArgumentException("Trainee not found with username: "
                    + trainingRequest.getTrainee().getUsername());
        }
        training.setTrainee(trainee.get());

        var trainer = trainerRepository.findByUsername(trainingRequest.getTrainer().getUsername());
        if (trainer.isEmpty()) {
            throw new IllegalArgumentException("Trainer not found with username: "
                    + trainingRequest.getTrainer().getUsername());
        }
        training.setTrainer(trainer.get());
        String trainingName = trainingRequest.getTrainingType().getTrainingTypeName().getName().toUpperCase();
        training.setTrainingType(trainingTypeRepository.findByName(trainingName));

        return training;
    }
}
