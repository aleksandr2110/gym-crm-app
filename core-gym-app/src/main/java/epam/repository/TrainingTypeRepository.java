package epam.repository;

import epam.domain.entity.TrainingType;
import epam.domain.entity.TrainingTypeName;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class TrainingTypeRepository {

    private final EntityManager entityManager;

    public TrainingTypeRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void saveTrainingType(List<TrainingType> trainingList) {
        for (TrainingType training: trainingList) {
            entityManager.persist(training);
        }
    }

    public TrainingType findByName(String name) {
        try {
            Query query = entityManager.createQuery(
                    "FROM TrainingType t WHERE t.trainingTypeName = :name",
                    TrainingType.class);
            query.setParameter("name", TrainingTypeName.getByName(name));
            return (TrainingType) query.getSingleResult();
        } catch (NoResultException e) {
            log.warn("TrainingType not found with name: {}", name);
            return null;
        }
    }

    public TrainingType findById(Long id) {
        try {
            TrainingType trainingType = entityManager.find(TrainingType.class, id);
            return trainingType;
        } catch (NoResultException e) {
            log.warn("TrainingType not found with id: {}", id);
            return null;
        }
    }

    public List<TrainingType> findAll() {
        Query query = entityManager.createQuery(
                "SELECT t FROM TrainingType t",
                TrainingType.class);
        return query.getResultList();
    }
}
