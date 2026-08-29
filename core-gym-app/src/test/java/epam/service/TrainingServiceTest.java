package epam.service;

import epam.domain.entity.*;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.repository.TrainingRepository;
import epam.repository.TrainingTypeRepository;
import epam.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void shouldSaveTraining() {
        //GIVEN
        Long userId = 1L;

        var trainee = new Trainee();
        trainee.setId(userId);
        trainee.setUsername("Viliam.Dashkovec");

        var trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeName.getByName("TypeScript"));

        var trainer = new Trainer();
        trainer.setUsername("Dmitriy.Gordon");

        var training = new Training();
        training.setTrainingType(trainingType);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingName("Learning TypeScript");
        training.setTrainingDate(LocalDateTime.of(2026, Month.OCTOBER, 13, 12, 15, 00));
        training.setTrainingDuration(45);
        training.setTrainingName("TypeScript");

        //WHEN
        when(traineeRepository.findByUsername(any())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName(any())).thenReturn(trainingType);
        doNothing().when(trainingRepository).save(any());
        trainingService.save(training);

        //THEN
        verify(trainingRepository, times(1)).save(any());
    }

    @Test
    void shouldSelectTrainingById() {
        //GIVEN
        Long userId = 2L;

        var trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeName.getByName("JavaScript"));

        Training training = new Training();
        training.setTrainingName("Learning JavaScript");
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDateTime.of(2026, 10, 13, 12, 15, 00));
        training.setTrainingDuration(60);

        //WHEN
        when(trainingRepository.findTrainingById(userId)).thenReturn(training);
        var createdTraining = trainingService.findTrainingById(userId);

        //THEN
        assertNotNull(createdTraining);
        assertEquals(training.getTrainingName(), createdTraining.getTrainingName());
        assertEquals(training.getTrainingDate(), createdTraining.getTrainingDate());
        assertEquals(training.getTrainingDuration(), createdTraining.getTrainingDuration());
    }

    @Test
    void shouldSelectTrainingByTrainingTypeName() {
        //GIVEN
        var trainingType = new TrainingType();
        trainingType.setTrainingTypeName(TrainingTypeName.getByName("JavaScript"));

        Training training = new Training();
        training.setTrainingName("Learning JavaScript");
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDateTime.of(2026, 10, 15, 12, 15, 00));
        training.setTrainingDuration(50);
        List<Training> trainingList = new ArrayList<>();
        trainingList.add(training);

        //WHEN
        when(trainingRepository.getTrainingByTrainingTypeName(
                TrainingTypeName.getByName("JavaScript").name())).thenReturn(trainingList);
        List<Training> createdTrainingList = trainingService.getTrainingByTrainingTypeName(
                TrainingTypeName.getByName("JavaScript").name());

        //THEN
        assertEquals(trainingList.size(), createdTrainingList.size());
        assertEquals(trainingList.get(0).getTrainingName(), createdTrainingList.get(0).getTrainingName());
    }
}
