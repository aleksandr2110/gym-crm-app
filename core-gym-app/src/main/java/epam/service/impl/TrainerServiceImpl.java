package epam.service.impl;

import epam.annotation.ExecutionTime;
import epam.constants.RoleName;
import epam.domain.dto.request.UpdateTrainerRequestDTO;
import epam.domain.entity.Trainee;
import epam.domain.entity.Trainer;
import epam.domain.entity.Training;
import epam.domain.entity.TrainingTypeName;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.repository.TrainingRepository;
import epam.security.service.RoleService;
import epam.service.TrainerService;
import epam.service.TrainingTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainingTypeService trainingTypeService;
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public TrainerServiceImpl(TrainingTypeService trainingTypeService,
                              TrainingRepository trainingRepository,
                              TraineeRepository traineeRepository,
                              TrainerRepository trainerRepository,  PasswordEncoder passwordEncoder,
                              RoleService roleService) {
        this.trainingTypeService = trainingTypeService;
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void beforeCreate(Trainer entity) {
        roleService.assignRoleToTrainer(entity, RoleName.ROLE_TRAINER);
    }

    @Override
    @Transactional
    public Trainer save(Trainer trainer, String specialization) {
        var trainingType = trainingTypeService.findByName(specialization.toUpperCase());
        trainer.setSpecialization(trainingType);

        List<Trainee> traineeList = new ArrayList<>();
        for (Trainee trainee: trainer.getTrainees()) {
            var savedTrainee = traineeRepository.findById(trainee.getId());
            savedTrainee.ifPresent(traineeList::add);
        }
        trainer.setTrainees(traineeList);

        List<Training> trainingsList = new ArrayList<>();
        for (Training training: trainer.getTrainings()) {
            var savedTraining = trainingRepository.findTrainingById(training.getId());
            trainingsList.add(savedTraining);
        }
        trainer.setTrainings(trainingsList);

        String plainPassword = trainer.getPassword();
        trainer.setPassword(passwordEncoder.encode(plainPassword));

        return trainerRepository.save(trainer);
    }

    @Override
    @ExecutionTime
    public Trainer findById(Long id) {
        var trainer = trainerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Trainer not found with id: " + id));

        return trainer;
    }

    @Override
    @Transactional
    public Trainer findByUsername(String userName) {
        var trainer = trainerRepository.findByUsername(userName).orElseThrow(
                () -> new IllegalArgumentException("Trainer not found with username: " + userName));

        return trainer;
    }

    @Override
    @ExecutionTime
    public void changePassword(Long id, String newPassword) {
        trainerRepository.changePassword(id, newPassword);
    }

    @Transactional
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        var trainer = trainerRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("Trainer not found with username: " + username));
        if (!passwordEncoder.matches(oldPassword, trainer.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        trainerRepository.changePassword(username, newPassword);
    }

    @Transactional
    @Override
    public Trainer updateProfile(UpdateTrainerRequestDTO requestTrainer) {
        Trainer currentTrainer = trainerRepository.findByUsername(requestTrainer.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: "
                        + requestTrainer.getUsername())
        );
        currentTrainer.setUsername(requestTrainer.getUsername());
        currentTrainer.setFirstName(requestTrainer.getFirstName());
        currentTrainer.setLastName(requestTrainer.getLastName());

        var trainingTypeName = TrainingTypeName.getByName(requestTrainer.getSpecialization().toUpperCase());
        var trainingType = trainingTypeService.findByName(trainingTypeName.getName().toUpperCase());
        currentTrainer.setSpecialization(trainingType);
        currentTrainer.setActive(requestTrainer.getIsActive());

        return trainerRepository.save(currentTrainer);
    }

    @Transactional
    @Override
    public void activateDeactivateTrainee(String username, boolean isActive) {
        var entity = trainerRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("Trainee not found with id: " + username));

        trainerRepository.toggleStatus(entity.getId());
    }

    @Override
    public void deleteProfile(String username) {
        trainerRepository.delete(username);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerRepository.findAll();
    }

    @Override
    public List<String> findUsernamesLike(String likeUsername) {
        return trainerRepository.findUsernamesLike(likeUsername);
    }

    @Transactional
    @Override
    public List<Trainer> findAllNotAssignedToTrainee(String traineeUsername) {
        Trainee trainee = traineeRepository.findByUsername(traineeUsername).orElseThrow(()
                -> new IllegalArgumentException("Trainer not found with username: " + traineeUsername));
        if (trainee == null) {
            throw new NoSuchElementException("Trainee not found with username: " + traineeUsername);
        }
        return trainerRepository.findAllNotAssignedToTrainee(traineeUsername);
    }



}
