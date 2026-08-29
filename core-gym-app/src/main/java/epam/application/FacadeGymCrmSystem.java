package epam.application;

import epam.domain.dto.request.*;
import epam.domain.dto.response.*;
import epam.domain.entity.*;
import epam.service.TraineeService;
import epam.service.TrainerService;
import epam.service.TrainingService;
import epam.service.TrainingTypeService;
import epam.util.DataMapper;
import epam.util.UsernameAndPasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FacadeGymCrmSystem {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;
    private final DataMapper dataMapper;
    private final TrainingTypeService trainingTypeService;

    @Autowired
    public FacadeGymCrmSystem(TrainerService trainerService, TraineeService traineeService,
                              TrainingService trainingService, DataMapper dataMapper,
                              TrainingTypeService trainingTypeService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
        this.dataMapper = dataMapper;
        this.trainingTypeService = trainingTypeService;
    }

    @Transactional
    public RegistrationResponseDTO createTrainee(TraineeRequestDTO request) {
        var trainee = dataMapper.toTrainee(request);
        traineeService.beforeCreate(trainee);
        String password = setTraineeUsername(trainee);
        var createdTrainee = traineeService.save(trainee);

        RegistrationResponseDTO response = RegistrationResponseDTO.builder()
                .username(createdTrainee.getUsername())
                .password(password)
                .build();

        return response;
    }

    @Transactional
    public TraineeProfileDTO getTraineeByUsername(String username) {
        var trainee = traineeService.findByUsername(username);
        List<Trainer> trainers = trainee.getTrainers();
        List<TrainerInfoDTO> trainerDtos = new ArrayList<>();
        for (Trainer trainer : trainers) {
            trainerDtos.add(dataMapper.toTrainerDTO(trainer));
        }
        var traineeDTO = dataMapper.toProfileTraineeDTO(trainee);
        traineeDTO.setTrainers(trainerDtos);
        traineeDTO.setIsActive(trainee.isActive());
        return traineeDTO;
    }

    @Transactional
    public void deleteTrainee(String username) {
       traineeService.deleteProfile(username);
    }

    @Transactional
    public List<TrainerInfoDTO> getAvailableTrainers(String username) {
        List<Trainer> trainers = trainerService.findAllNotAssignedToTrainee(username);
        List<TrainerInfoDTO> trainerDtos = new ArrayList<>();
        for (Trainer trainer : trainers) {
            trainerDtos.add(dataMapper.toTrainerDTO(trainer));
        }
        return trainerDtos;
    }

    @Transactional
    public TraineeProfileDTO updateTraineeProfile(UpdateTraineeRequestDTO traineeRequestDTO) {
        var updateTrainee = dataMapper.toUpdateTrainee(traineeRequestDTO);
        updateTrainee.setDateOfBirth(LocalDate.parse(traineeRequestDTO.getDateOfBirth()));

        var updatedTrainee = traineeService.updateProfile(updateTrainee);
        TraineeProfileDTO traineeProfileDTO = dataMapper.toProfileTraineeDTO(updatedTrainee);
        traineeProfileDTO.setIsActive(updatedTrainee.isActive());
        return traineeProfileDTO;
    }

    @Transactional
    public void changeTraineePassword(ChangePasswordRequestDTO request) {
        traineeService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
    }

    @Transactional
    public void activateDeactivateTrainee(String username, Boolean isActive) {
        traineeService.activateDeactivateTrainee(username, isActive);
    }

    @Transactional
    public List<TrainerInfoDTO> updateTrainersList(UpdateTraineeTrainersRequestDTO request) {
        List<Trainer> newTrainers = traineeService.updateTrainersList(request.getTraineeUsername(),
                request.getTrainerUsernames());
        List<TrainerInfoDTO> result = newTrainers.stream()
                .map(dataMapper::toTrainerDTO)
                .toList();
        return result;
    }

    @Transactional
    public RegistrationResponseDTO createTrainer(TrainerRequestDTO request) {
        var trainer = dataMapper.toTrainer(request);
        String password = setTrainerUsername(trainer);
        trainerService.beforeCreate(trainer);
        var createdTrainer = trainerService.save(trainer, request.getSpecialization());

        RegistrationResponseDTO trainerDTO = RegistrationResponseDTO.builder()
                .username(createdTrainer.getUsername())
                .password(password)
                .build();
        return trainerDTO;
    }

    @Transactional
    public TrainerProfileDTO getTrainerByUsername(String username) {
        var trainer = trainerService.findByUsername(username);

        var trainerProfileDTO = dataMapper.toProfileTrainerDTO(trainer);
        trainerProfileDTO.setIsActive(trainer.isActive());
        trainerProfileDTO.setSpecialization(trainer.getSpecialization().getTrainingTypeName().getName());
        return trainerProfileDTO;
    }

    @Transactional
    public TrainerProfileDTO updateTrainerProfile(UpdateTrainerRequestDTO request) {
        var trainer = trainerService.updateProfile(request);

        var trainerProfileDTO = dataMapper.toProfileTrainerDTO(trainer);
        trainerProfileDTO.setSpecialization(trainer.getSpecialization().getTrainingTypeName().getName());
        trainerProfileDTO.setIsActive(trainer.isActive());

        return trainerProfileDTO;
    }

    @Transactional
    public void changeTrainerPassword(ChangePasswordRequestDTO request) {
        trainerService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
    }

    @Transactional
    public void activateDeactivateTrainer(String username, Boolean isActive) {
        trainerService.activateDeactivateTrainee(username, isActive);
    }

    public List<TrainingTypeDTO> getTrainingTypes() {
        var trainingTypes = trainingTypeService.findAll();
        List<TrainingTypeDTO> types = trainingTypes.stream()
                .map(trainingType -> {
                    var trainingTypeDTO = new TrainingTypeDTO();
                    trainingTypeDTO.setId(trainingType.getId());
                    trainingTypeDTO.setTrainingTypeName(trainingType.getTrainingTypeName().name());
                    return trainingTypeDTO;
                }).toList();
        return types;
    }

    public void createTraining(TrainingRequestDTO trainingRequest) {
        var training = dataMapper.toTraining(trainingRequest);
        var trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeName.getByName(trainingRequest.getTrainingType().toUpperCase()));

        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDateTime.parse(trainingRequest.getTrainingDate()));
        trainingService.save(training);
    }

    @Transactional
    public List<TrainingTraineeDTO> getTraineeTrainings(TraineeTrainingsRequestDTO filterRequest) {
        String username = filterRequest.getUsername();
        LocalDateTime fromDate = LocalDateTime.parse(filterRequest.getPeriodFrom());
        LocalDateTime toDate  = LocalDateTime.parse(filterRequest.getPeriodTo());
        String trainingType = filterRequest.getTrainingType();

        List<Training> trainingsList = trainingService.selectTraineeTrainings(username, fromDate,
                toDate, trainingType);
        List<TrainingTraineeDTO> trainingTraineeDTOS = trainingsList.stream().map(
                training -> {
                    var trainingResponse = new TrainingTraineeDTO();
                    trainingResponse.setTrainingName(training.getTrainingName());
                    trainingResponse.setTrainingType(training.getTrainingType().getTrainingTypeName().getName());
                    trainingResponse.setTrainingDate(training.getTrainingDate());
                    trainingResponse.setTrainingDuration(training.getTrainingDuration());
                    trainingResponse.setTrainerName(training.getTrainer().getUsername());
                    return trainingResponse;
                }
        ).toList();

        return trainingTraineeDTOS;
    }

    @Transactional
    public List<TrainingTrainerDTO> getTrainerTrainings(TrainerTrainingsRequestDTO filterRequest) {
        String trainerUsername = filterRequest.getUsername();
        LocalDateTime fromDate = LocalDateTime.parse(filterRequest.getPeriodFrom());
        LocalDateTime toDate  = LocalDateTime.parse(filterRequest.getPeriodTo());

        List<Training> trainings = trainingService.selectTrainerTrainings(trainerUsername, fromDate, toDate);

        List<TrainingTrainerDTO> trainingDTOs = trainings.stream().map(
                training -> {
                    var trainingResponse = new TrainingTrainerDTO();
                    trainingResponse.setTrainingName(training.getTrainingName());
                    trainingResponse.setTrainingType(training.getTrainingType().getTrainingTypeName().getName());
                    trainingResponse.setTrainingDate(training.getTrainingDate());
                    trainingResponse.setTrainingDuration(training.getTrainingDuration());
                    trainingResponse.setTraineeName(training.getTrainee().getUsername());
                    return trainingResponse;
                }
        ).toList();
        return trainingDTOs;
    }

    private String setTraineeUsername(Trainee trainee) {
        if (trainee.getUsername() != null) {
            throw new IllegalArgumentException("Attempt to save trainee with username: "
                    + trainee.getUsername());
        }
        trainee.setUsername(UsernameAndPasswordGenerator.createUsername(
                trainee.getFirstName(),
                trainee.getLastName()));
        trainee.setPassword(UsernameAndPasswordGenerator.generatePassword());
        List<String> usernameDuplicates = traineeService.findUsernamesLike(trainee.getFirstName() + "%");
        trainee.setUsername(trainee.getUsername() + (usernameDuplicates.size() == 0 ? "" : usernameDuplicates.size()));
        return trainee.getPassword();
    }

    private String setTrainerUsername(Trainer trainer) {
        trainer.setUsername(UsernameAndPasswordGenerator.createUsername(
                trainer.getFirstName(),
                trainer.getLastName()));
        trainer.setPassword(UsernameAndPasswordGenerator.generatePassword());
        List<String> usernameDuplicates = trainerService.findUsernamesLike(trainer.getUsername() + "%");
        trainer.setUsername(trainer.getUsername() + (usernameDuplicates.size() == 0 ? "" : usernameDuplicates.size()));
        return trainer.getPassword();
    }
}
