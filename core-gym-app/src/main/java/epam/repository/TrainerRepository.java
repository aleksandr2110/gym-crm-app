package epam.repository;

import epam.domain.entity.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class TrainerRepository implements EntityRepository<Trainer, Long> {

    private final EntityManager entityManager;

    public TrainerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer.getId() == null) {
            entityManager.persist(trainer);
        } else {
            trainer = entityManager.merge(trainer);
        }
        log.info("Trainer saved with username: {}", trainer.getUsername());
        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        var entity = entityManager.find(Trainer.class, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        TypedQuery<Trainer> query = entityManager.createQuery(
                "FROM Trainer t WHERE t.username = :username", Trainer.class);
        query.setParameter("username", username);
        return query.getResultStream().findAny();
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        var entity = findById(id).orElseThrow(()
                -> new IllegalArgumentException("Trainer not found with id: " + id));

        entity.setPassword(newPassword);
        entityManager.merge(entity);
        log.info("Password changed for trainer with id: {}", id);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        Trainer entity = findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("Trainer not found with username: " + username));

        entity.setPassword(newPassword);
        entityManager.merge(entity);
        log.info("Password changed for trainee with user name: {}", username);
    }

    @Override
    public void toggleStatus(Long id) {
        var entity = findById(id).orElseThrow(
                () -> new IllegalArgumentException("Trainer not found with id: " + id));
        entity.setActive(!entity.isActive());
        entityManager.merge(entity);
        log.info("Trainer activated with id: {}", id);
    }

    @Override
    public void delete(String username) {
        var entity = findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("Trainer not found with username: " + username));
        entityManager.remove(entity);
        log.info("Trainer deleted with username: {}", username);
    }

    @Override
    public List<Trainer> findAll() {
        Query query = entityManager.createQuery(
                "SELECT t FROM Trainer t", Trainer.class);
        return query.getResultList();
    }

    @Override
    public List<String> findUsernamesLike(String likeUsername) {
        return entityManager.createQuery(
                        "select t.username from Trainer t where t.username like :username", String.class)
                .setParameter("username", likeUsername)
                .getResultList();
    }
}
