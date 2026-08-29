package epam.service;

import epam.domain.dto.request.TraineeRequestDTO;
import epam.domain.dto.request.UpdateTraineeRequestDTO;
import epam.domain.dto.response.RegistrationResponseDTO;
import epam.domain.dto.response.TraineeProfileDTO;
import epam.domain.dto.response.TrainerInfoDTO;
import epam.domain.entity.Trainee;
import epam.domain.entity.Trainer;

import java.util.List;

public interface TraineeService {

    void beforeCreate(Trainee entity);
    Trainee save(Trainee trainee);
    Trainee findById(Long id);
    Trainee findByUsername(String userName);
    void changePassword(String username, String oldPassword, String newPassword);
    void changePassword(String username, String newPassword);
    Trainee updateProfile(Trainee updateTrainee);
    void activateDeactivateTrainee(String username, boolean isActive);
    Trainee authenticateTrainee(String username, String password);
    void deleteProfile(String username);
    List<Trainer> updateTrainersList(String traineeUsername, List<String> trainerUsernames);
    List<String> findUsernamesLike(String likeUsername);
}
