package epam.repository;

import epam.domain.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface EntityRepository<T, ID> {

    T save(T entity);
    Optional<T> findById(ID id);
    Optional<T> findByUsername(String userName);
    void changePassword(Long id, String newPassword);
    void changePassword(String username, String newPassword);
    void toggleStatus(Long id);
    void delete(String username);
    default List<Trainer> findAllNotAssignedToTrainee(String traineeUsername) {
        return List.of();
    }
    List<T> findAll();
    List<String> findUsernamesLike(String likeUsername);
}
