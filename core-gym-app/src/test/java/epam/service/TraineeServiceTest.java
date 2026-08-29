package epam.service;


import epam.domain.entity.Trainee;
import epam.repository.TraineeRepository;
import epam.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void shouldSaveTrainee() {
        //GIVEN
        var traineeRequest = new Trainee();
        traineeRequest.setFirstName("Alex");
        traineeRequest.setLastName("Hofman");
        traineeRequest.setAddress("23 Road star st");
        traineeRequest.setActive(true);

        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("Alex");
        trainee.setLastName("Hofman");
        trainee.setAddress("23 Road star st");
        trainee.setActive(true);

        //WHEN
        when(traineeRepository.save(any())).thenReturn(trainee);
        Trainee createdTrainee = traineeService.save(traineeRequest);

        //THEN
        assertNotNull(createdTrainee);
        assertEquals(traineeRequest.getFirstName(), createdTrainee.getFirstName());
        assertEquals(traineeRequest.getLastName(), createdTrainee.getLastName());
        assertEquals(traineeRequest.getAddress(), createdTrainee.getAddress());
        assertTrue(createdTrainee.isActive());
        verify(traineeRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateTrainee() {
        //GIVEN
        Long userId = 1L;
        String username = "Rerg.Grill";

        var currentTrainee = new Trainee();
        currentTrainee.setId(userId);
        currentTrainee.setFirstName("Rerg");
        currentTrainee.setLastName("Grill");
        currentTrainee.setAddress("23 Road star st");
        currentTrainee.setActive(true);

        var traineeRequest = new Trainee();
        traineeRequest.setId(userId);
        traineeRequest.setUsername(username);
        traineeRequest.setFirstName("Alex");
        traineeRequest.setLastName("Hofman");
        traineeRequest.setAddress("23 Road star st");
        traineeRequest.setActive(true);

        var updatedTrainee = new Trainee();
        updatedTrainee.setId(traineeRequest.getId());
        updatedTrainee.setFirstName(traineeRequest.getFirstName());
        updatedTrainee.setLastName(traineeRequest.getLastName());
        updatedTrainee.setAddress(traineeRequest.getAddress());
        updatedTrainee.setActive(traineeRequest.isActive());

        //WHEN
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(currentTrainee));
        when(traineeRepository.save(any())).thenReturn(updatedTrainee);
        Trainee updatedTraineeById = traineeService.updateProfile(traineeRequest);

        //THEN
        assertNotNull(updatedTraineeById);
        assertEquals(traineeRequest.getFirstName(), updatedTraineeById.getFirstName());
        assertEquals(traineeRequest.getLastName(), updatedTraineeById.getLastName());
        assertEquals(traineeRequest.getAddress(), updatedTraineeById.getAddress());
        assertTrue(updatedTraineeById.isActive());
    }

    @Test
    void shouldThrowExceptionWhenUpdateTrainee() {
        //GIVEN
        String username = "Antony.Gustava";
        var traineeRequest = new Trainee();
        traineeRequest.setUsername(username);

        //WHEN
        when(traineeRepository.findByUsername(traineeRequest.getUsername()))
                .thenThrow(new IllegalArgumentException("Trainee not found with username: " + username));

        //THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeService.updateProfile(traineeRequest);
        });
        assertEquals("Trainee not found with username: " + username, exception.getMessage());
    }

    @Test
    void shouldSelectTraineeById() {
        //GIVEN
        Long userId = 1L;
        var trainee = new Trainee();
        trainee.setFirstName("Rerg");
        trainee.setLastName("Grill");
        trainee.setAddress("67 Road star st");
        trainee.setActive(false);

        //WHEN
        when(traineeRepository.findById(userId)).thenReturn(Optional.of(trainee));
        var selectedTraineeById = traineeService.findById(userId);

        //THEN
        assertNotNull(selectedTraineeById);
        assertEquals(trainee.getFirstName(), selectedTraineeById.getFirstName());
        assertEquals(trainee.getLastName(), selectedTraineeById.getLastName());
        assertEquals(trainee.getAddress(), selectedTraineeById.getAddress());
        assertFalse(selectedTraineeById.isActive());
    }

    @Test
    void shouldDeleteTraineeById() {
        //GIVEN
        String userName = "oleksandr.kypriy";

        //WHEN
        traineeService.deleteProfile(userName);

        //THEN
        verify(traineeRepository, times(1)).delete(userName);
    }
}
