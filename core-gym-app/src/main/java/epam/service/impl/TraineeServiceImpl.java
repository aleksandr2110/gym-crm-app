package epam.service.impl;

import epam.annotation.ExecutionTime;
import epam.constants.Permission;
import epam.constants.RoleName;
import epam.controller.exception.UnauthorizedException;
import epam.domain.entity.Trainee;
import epam.domain.entity.Trainer;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.security.service.RoleService;
import epam.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository, TrainerRepository trainerRepository,
                              PasswordEncoder passwordEncoder,
                              RoleService roleService) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public void beforeCreate(Trainee entity) {
        roleService.assignRoleToTrainee(entity,
                RoleName.ROLE_TRAINEE);
    }

    @Transactional
    @Override
    public Trainee save(Trainee trainee) {
        String plainPassword = trainee.getPassword();
        trainee.setPassword(passwordEncoder.encode(plainPassword));
        return traineeRepository.save(trainee);
    }

    @Override
    @ExecutionTime
    public Trainee findById(Long id) {
        Trainee trainee = traineeRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Trainee not found with id: " + id));

        return trainee;
    }

    @Transactional
    @Override
    public Trainee findByUsername(String userName) {
        var trainee = traineeRepository.findByUsername(userName).orElseThrow(
                () -> new IllegalArgumentException("Trainee not found with username: " + userName));

        return trainee;
    }

    @Transactional
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        var trainee = traineeRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("Trainee not found with username: " + username));
        if (!passwordEncoder.matches(oldPassword, trainee.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        traineeRepository.changePassword(trainee.getUsername(), newPassword);
    }

    @Transactional
    @Override
    public void changePassword(String username, String newPassword) {
        traineeRepository.changePassword(username, newPassword);
    }

    @Transactional
    @Override
    public Trainee updateProfile(Trainee trainee) {
        Trainee currentTrainee = traineeRepository.findByUsername(trainee.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("Trainee not found with username: " + trainee.getUsername())
        );

        currentTrainee.setFirstName(trainee.getFirstName());
        currentTrainee.setLastName(trainee.getLastName());
        currentTrainee.setAddress(trainee.getAddress());
        currentTrainee.setDateOfBirth(trainee.getDateOfBirth());
        currentTrainee.setActive(trainee.isActive());
        return traineeRepository.save(currentTrainee);
    }

    @Transactional
    @Override
    public void activateDeactivateTrainee(String username, boolean isActive) {
        var entity = traineeRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("Trainee not found with username : " + username));

        traineeRepository.toggleStatus(entity.getId());
    }

    @Transactional
    @Override
    public Trainee authenticateTrainee(String username, String password) {
        if (username == null || password == null) {
            throw new UnauthorizedException("Trainee is not authenticated");
        }
        if (username.equals("") || password.equals("")) {
            throw new UnauthorizedException("Trainee is not authenticated");
        }
        var entity = traineeRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("Trainee not found with username: " + username));
        if (!entity.getPassword().equals(password)) {
            throw new UnauthorizedException("Trainee is not authenticated: " + username);
        }
        return entity;
    }

    @Transactional
    @Override
    public void deleteProfile(String username) {
        traineeRepository.delete(username);
    }

    @Override
    @Transactional
    public List<Trainer> updateTrainersList(String traineeUsername, List<String> trainerUsernames) {
        var trainee = traineeRepository.findByUsername(traineeUsername).orElseThrow(()
                -> new IllegalArgumentException("Trainee not found with username: " + traineeUsername));

        List<Trainer> newTrainers = trainerUsernames.stream()
                .map(username -> {
                    Trainer trainer = trainerRepository.findByUsername(username).orElseThrow(()
                            -> new IllegalArgumentException("Trainer not found with username: " + username));
                    return trainer;
                })
                .toList();

        trainee.setTrainers(new ArrayList<>(newTrainers));
        traineeRepository.save(trainee);

        return newTrainers;
    }

    @Override
    @Transactional
    public List<String> findUsernamesLike(String likeUsername) {
        return traineeRepository.findUsernamesLike(likeUsername);
    }

}
