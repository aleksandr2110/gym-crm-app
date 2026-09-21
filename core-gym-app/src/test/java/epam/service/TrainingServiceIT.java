package epam.service;

import epam.domain.entity.Trainee;
import epam.domain.entity.Trainer;
import epam.domain.entity.TrainingType;
import epam.domain.entity.TrainingTypeName;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TrainingServiceIT {

    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final EntityManager entityManager;

    @Autowired
    public TrainingServiceIT(EntityManager entityManager, TrainerRepository trainerRepository,
                             TraineeRepository traineeRepository, TrainerService trainerService,
                             TraineeService traineeService, TrainingService trainingService) {
        this.entityManager = entityManager;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    private String traineeUsername;
    private String trainerUsername;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers_trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();

//        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
//        entityManager.createNativeQuery("ALTER TABLE trainings ALTER COLUMN id RESTART WITH 1").executeUpdate();
//        entityManager.flush();

        var trainee = new Trainee();
        trainee.setFirstName("Josh");
        trainee.setLastName("Blog");
        trainee.setUsername("Josh.Blog");
        trainee.setDateOfBirth(LocalDate.of(1988, 10, 1));
        trainee.setAddress("24 Red St");
        trainee.setPassword("fgfdgdgw2");
        trainee.setActive(true);
        var traineeResponse = traineeService.save(trainee);
        traineeUsername = traineeResponse.getUsername();

        var trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeName.getByName("JAVA"));

        var trainer = new Trainer();
        trainer.setFirstName("Gerbert");
        trainer.setLastName("Shild");
        trainer.setUsername("Gerbert.Shild");
        trainer.setPassword("gyuyguyg7");
        trainer.setSpecialization(trainingType);
        var trainerResponse = trainerService.save(trainer, "JAVA");
        trainerUsername = trainerResponse.getUsername();

        entityManager.flush();
    }


}
