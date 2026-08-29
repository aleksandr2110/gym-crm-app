package epam.repository;

import epam.domain.entity.Trainee;
import epam.domain.entity.Trainer;
import epam.domain.entity.Training;
import epam.domain.entity.TrainingTypeName;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Repository
public class TrainingRepository {

    private final EntityManager entityManager;

    public TrainingRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(Training training) {
        if (training.getId() == null) {
            entityManager.persist(training);
            log.info("Training created: {}", training.getTrainingName());
        } else {
            entityManager.merge(training);
            log.info("Training updated: {}", training.getTrainingName());
        }
    }

    public Training findTrainingById(Long id) {
        var training = entityManager.find(Training.class, id);
        if (training == null) {
            log.warn("Training not found with id: {}", id);
        }

        return training;
    }
    public List<Training> getTrainingByTrainingTypeName(String trainingTypeName) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT t FROM Training t WHERE t.trainingType.trainingTypeName = :trainingTypeName", Training.class);
            query.setParameter("trainingTypeName", TrainingTypeName.getByName(trainingTypeName));
            return (List<Training>) query.getResultList();
        } catch (NoResultException e) {
            log.warn("Trainer not found with training type name: {}", trainingTypeName);
            return null;
        }
    }

    public List<Training> findTraineeTrainingsByUserNameAndDate(String traineeUsername,
                                                                LocalDateTime fromDate,
                                                                LocalDateTime toDate,
                                                                String trainingType) {
        return buildTrainingsQuery(training -> {
            Join<Training, Trainee> trainee = training.join("trainee");
            return trainee.get("username");
        }, traineeUsername, fromDate, toDate, trainingType);
    }

    public List<Training> findTrainerTrainingsByUserNameAndDate(String trainerUsername,
                                                                LocalDateTime fromDate,
                                                                LocalDateTime toDate) {
        return buildTrainingsQuery(training -> {
            Join<Training, Trainer> trainer = training.join("trainer");
            return trainer.get("username");
        }, trainerUsername, fromDate, toDate, null);
    }

    private List<Training> buildTrainingsQuery(Function<Root<Training>, Expression<String>> usernameGetter,
                                               String userName, LocalDateTime fromDate,
                                               LocalDateTime toDate, String trainingType) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> training = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(usernameGetter.apply(training), userName));
        predicates = addDatePredicates(cb, training, fromDate, toDate, predicates, trainingType);

        query.select(training).where(cb.and(predicates.toArray(new Predicate[0])));
        Query squery = entityManager.createQuery(query);
        return squery.getResultList();
    }

    private List<Predicate> addDatePredicates(CriteriaBuilder cb, Root<Training> training,
                                              LocalDateTime fromDate, LocalDateTime toDate, List<Predicate> predicates,
                                              String trainingType) {
        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), toDate));
        }
        if (trainingType != null && !trainingType.isBlank()) {
            predicates.add(cb.equal(training.get("trainingType").get("trainingTypeName"),
                    TrainingTypeName.getByName(trainingType.toUpperCase())));
        }
        return predicates;
    }

    public List<Training> findAll() {
        Query query = entityManager.createQuery(
                "SELECT t FROM Training t", Training.class);
        return query.getResultList();
    }
}
