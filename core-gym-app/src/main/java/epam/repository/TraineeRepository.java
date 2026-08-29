package epam.repository;


import epam.domain.entity.Trainee;
import epam.domain.entity.Trainer;
import epam.domain.entity.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class TraineeRepository implements EntityRepository<Trainee, Long> {

    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TraineeRepository(EntityManager entityManager, PasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Trainee save(Trainee trainee) {
        if (trainee.getId() == null) {
            entityManager.persist(trainee);
        } else {
            trainee = entityManager.merge(trainee);
        }

        log.info("Trainee saved with username: {}", trainee.getUsername());
        return trainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        var entity = entityManager.find(Trainee.class, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        TypedQuery<Trainee> query = entityManager.createQuery(
                        "FROM Trainee t WHERE t.username = :username", Trainee.class)
                .setParameter("username", username);
        return query.getResultStream().findAny();
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        var entity = findById(id).orElseThrow(()
                -> new IllegalArgumentException("Trainee not found with id: " + id));

        entity.setPassword(newPassword);
        entityManager.merge(entity);
        log.info("Password changed for trainee with id: {}", id);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        Trainee entity = findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("Trainee not found with username: " + username));

        entity.setPassword(passwordEncoder.encode(newPassword));
        entityManager.merge(entity);
        log.info("Password changed for trainee with user name: {}", username);
    }

    @Override
    public void toggleStatus(Long id) {
        var entity = findById(id).orElseThrow(
                () -> new IllegalArgumentException("Trainee not found with id: " + id));
        entity.setActive(!entity.isActive());
        entityManager.merge(entity);
        log.info("Trainee activated with id: {}", id);
    }

    @Override
    public void delete(String username) {
        var entity = findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("Trainee not found with username: " + username));

        Iterator<Trainer> trainerIterator = entity.getTrainers().iterator();
        while (trainerIterator.hasNext()) {
            var trainer = trainerIterator.next();
            trainerIterator.remove();
        }
        Iterator<Training> trainingIterator = entity.getTrainings().iterator();
        while (trainingIterator.hasNext()) {
            var training = trainingIterator.next();
            trainingIterator.remove();
        }

        entityManager.remove(entity);
        entityManager.flush();;

        log.info("Trainee deleted with username: {}", username);
    }

    @Override
    public List<Trainee> findAll() {
        Query query = entityManager.createQuery(
                "SELECT t FROM Trainee t", Trainee.class);
        return query.getResultList();
    }

    @Override
    public List<String> findUsernamesLike(String likeUsername) {
        return entityManager.createQuery(
                        "select t.username from Trainee t where t.username like :username", String.class)
                .setParameter("username", likeUsername)
                .getResultList();
    }
}
