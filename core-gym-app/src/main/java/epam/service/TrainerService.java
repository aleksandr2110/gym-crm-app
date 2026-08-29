package epam.service;

import epam.domain.dto.request.UpdateTrainerRequestDTO;
import epam.domain.entity.Trainer;

import java.util.List;

public interface TrainerService {

    void beforeCreate(Trainer entity);
    Trainer save(Trainer trainer, String specialization);
    Trainer findById(Long id);
    Trainer findByUsername(String userName);
    void changePassword(Long id, String newPassword);
    void changePassword(String username, String oldPassword, String newPassword);
    Trainer updateProfile(UpdateTrainerRequestDTO requestTrainer);
    void  activateDeactivateTrainee(String username, boolean isActive);
    void deleteProfile(String username);
    List<Trainer> findAll();
    List<String> findUsernamesLike(String likeUsername);
    List<Trainer> findAllNotAssignedToTrainee(String traineeUsername);
}
