package epam.service;

import epam.domain.dto.request.UpdateTrainerRequestDTO;
import epam.domain.entity.*;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.repository.TrainingRepository;
import epam.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainingTypeService trainingTypeService;
    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void shouldSaveTrainer() {
        //GIVEN
        Long userId = 1L;
        var trainee = new Trainee();
        trainee.setId(userId);

        var training = new Training();
        training.setId(userId);
        training.setTrainingName("PHP");

        var trainerRequest = new Trainer();
        trainerRequest.setFirstName("Sergey");
        trainerRequest.setLastName("Hofman");

        var trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeName.getByName("PHP"));

        trainerRequest.setSpecialization(trainingType);
        trainerRequest.setActive(true);
        trainerRequest.setTrainees(Arrays.asList(trainee));
        List<Training> trainingSet = new ArrayList<>();
        trainingSet.add(training);
        trainerRequest.setTrainings(trainingSet);

        var newTrainer = trainerRequest;

        //WHEN
        when(trainingTypeService.findByName(any())).thenReturn(trainingType);
        when(traineeRepository.findById(any())).thenReturn(Optional.of(trainee));
        when(trainingRepository.findTrainingById(any())).thenReturn(training);
        when(trainerRepository.save(any())).thenReturn(newTrainer);
        var createdTrainer = trainerService.save(trainerRequest, "PHP");

        //THEN
        assertNotNull(createdTrainer);
        assertEquals(trainerRequest.getFirstName(), createdTrainer.getFirstName());
        assertEquals(trainerRequest.getLastName(), createdTrainer.getLastName());
        assertEquals(trainerRequest.getSpecialization(), createdTrainer.getSpecialization());
        assertTrue(createdTrainer.isActive());
        verify(trainerRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateTrainer() {
        //GIVEN
        Long userId = 1L;
        String username = "David.Gosling";

        var training = new Training();
        training.setId(userId);
        training.setTrainingName("PHP");

        var trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeName.getByName("PHP"));

        var trainerRequest = new Trainer();
        trainerRequest.setId(userId);
        trainerRequest.setFirstName("Alexandr");
        trainerRequest.setLastName("Kirichenko");
        trainerRequest.setSpecialization(trainingType);
        trainerRequest.setActive(true);

        var requestTrainer = new UpdateTrainerRequestDTO();
        requestTrainer.setUsername("David.Gosling");
        requestTrainer.setSpecialization("PHP");
        requestTrainer.setIsActive(true);

        var currentTrainer = new Trainer();
        currentTrainer.setId(userId);
        currentTrainer.setFirstName("Rerg");
        currentTrainer.setLastName("Grill");
        currentTrainer.setSpecialization(trainingType);
        currentTrainer.setActive(true);

        var updatedTrainer = new Trainer();
        updatedTrainer.setId(trainerRequest.getId());
        updatedTrainer.setFirstName(trainerRequest.getFirstName());
        updatedTrainer.setLastName(trainerRequest.getLastName());
        updatedTrainer.setSpecialization(trainerRequest.getSpecialization());
        updatedTrainer.setActive(trainerRequest.isActive());

        //WHEN
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(currentTrainer));
        when(trainerRepository.save(currentTrainer)).thenReturn(updatedTrainer);
        when(trainingTypeService.findByName(any())).thenReturn(trainingType);
        var updatedNewTrainer = trainerService.updateProfile(requestTrainer);

        //THEN
        assertNotNull(updatedNewTrainer);
        assertEquals(trainerRequest.getFirstName(), updatedNewTrainer.getFirstName());
        assertEquals(trainerRequest.getLastName(), updatedNewTrainer.getLastName());
        assertEquals(trainerRequest.getSpecialization(), updatedNewTrainer.getSpecialization());
        assertTrue(updatedNewTrainer.isActive());
    }

    @Test
    void shouldThrowExceptionWhenUpdateTrainer() {
        //GIVEN
        String username = "David.Gosling";
        UpdateTrainerRequestDTO requestTrainer = new UpdateTrainerRequestDTO();
        requestTrainer.setUsername(username);

        //WHEN
        when(trainerRepository.findByUsername(requestTrainer.getUsername())).thenThrow(
                new IllegalArgumentException("Trainer not found with username: " + requestTrainer.getUsername()));

        //THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.updateProfile(requestTrainer);
        });
        assertEquals("Trainer not found with username: " + username, exception.getMessage());
    }

    @Test
    void shouldSelectTrainerById() {
        //GIVEN
        Long userId = 1L;

        var trainer = new Trainer();
        trainer.setFirstName("Rerg");
        trainer.setLastName("Grill");
        var trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeName.getByName("Java"));
        trainer.setSpecialization(trainingType);
        trainer.setActive(false);

        //WHEN
        when(trainerRepository.findById(userId)).thenReturn(Optional.of(trainer));
        var selectedTrainerById = trainerService.findById(userId);

        //THEN
        assertNotNull(selectedTrainerById);
        assertEquals(trainer.getFirstName(), selectedTrainerById.getFirstName());
        assertEquals(trainer.getLastName(), selectedTrainerById.getLastName());
        assertEquals(trainer.getSpecialization(), selectedTrainerById.getSpecialization());
        assertFalse(selectedTrainerById.isActive());
    }
}
