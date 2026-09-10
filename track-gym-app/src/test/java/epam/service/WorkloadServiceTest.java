package epam.service;

import epam.domain.dto.request.WorkloadRequest;
import epam.domain.model.TrainerWorkload;
import epam.domain.repo.WorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkloadServiceTest {

    @Mock
    private WorkloadRepository workloadRepository;

    @InjectMocks
    private WorkloadService workloadService;

    @Captor
    private ArgumentCaptor<TrainerWorkload> workloadCaptor;

    private Map<String, TrainerWorkload> testStorage;

    @BeforeEach
    void setUp() {
        reset(workloadRepository);
        testStorage = new HashMap<>();

        lenient().when(workloadRepository.findByUsername(anyString()))
                .thenAnswer(inv -> {
            String username = inv.getArgument(0);
            return Optional.ofNullable(testStorage.get(username));
        });

        lenient().when(workloadRepository.save(any(TrainerWorkload.class)))
                .thenAnswer(inv -> {
            TrainerWorkload workload = inv.getArgument(0);
            testStorage.put(workload.getUsername(), workload);
            return workload;
        });
    }

    @Test
    void testShouldAddTrainingDuration() {
        WorkloadRequest request = WorkloadRequest.builder()
                .username("Kiany.rivz")
                .firstName("Kiany")
                .lastName("Rivz")
                .isActive(true)
                .trainingDate(LocalDateTime.of(2026, 9, 13, 18, 00, 00))
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();

        workloadService.processWorkload(request);

        verify(workloadRepository).save(workloadCaptor.capture());
        TrainerWorkload saved = workloadCaptor.getValue();

        assertNotNull(saved);
        assertEquals("Kiany.rivz", saved.getUsername());
        assertEquals("Kiany", saved.getFirstName());
        assertEquals("Rivz", saved.getLastName());
        assertTrue(saved.getIsActive());
        assertEquals(60, saved.getTotalDuration(2026, 9));
    }

    @Test
    void testShouldAccumulateTrainingDuration() {
        String username = "jeff.ex";

        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2026, 3, 10, 18, 00, 00), 90, WorkloadRequest.ActionType.ADD));
        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2026, 3, 15, 18, 00, 00), 60, WorkloadRequest.ActionType.ADD));
        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2026, 3, 20, 18, 00, 00), 50, WorkloadRequest.ActionType.ADD));

        verify(workloadRepository, times(3)).save(workloadCaptor.capture());
        TrainerWorkload finalWorkload = testStorage.get(username);
        assertEquals(200, finalWorkload.getTotalDuration(2026, 3));
    }

    @Test
    void testShouldSubtractTrainingDuration() {
        String username = "Sebastian.ingrosso";

        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2026, 9, 10, 18, 00, 00), 120, WorkloadRequest.ActionType.ADD));
        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2026, 9, 10, 18, 00, 00), 60, WorkloadRequest.ActionType.DELETE));

        verify(workloadRepository, times(2)).save(workloadCaptor.capture());
        TrainerWorkload finalWorkload = testStorage.get(username);
        assertEquals(60, finalWorkload.getTotalDuration(2026, 9));
    }

    @Test
    void testShouldNotAllowNegativeDuration() {
        String username = "Timyr.tayson";

        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2026, 9, 10, 18, 00, 00), 30, WorkloadRequest.ActionType.ADD));
        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2026, 9, 10, 18, 00, 00), 100, WorkloadRequest.ActionType.DELETE));

        verify(workloadRepository, times(2)).save(workloadCaptor.capture());
        TrainerWorkload finalWorkload = testStorage.get(username);
        assertEquals(0, finalWorkload.getTotalDuration(2026, 9));
    }

    @Test
    void testShouldStoreMultipleMountsAndYears() {
        String username = "Max.turner";

        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2025, 9, 10, 18, 00, 00), 60, WorkloadRequest.ActionType.ADD));
        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2026, 9, 10, 18, 00, 00), 90, WorkloadRequest.ActionType.ADD));
        workloadService.processWorkload(createRequest(username,
                LocalDateTime.of(2027, 9, 10, 18, 00, 00), 50, WorkloadRequest.ActionType.ADD));

        verify(workloadRepository, times(3)).save(workloadCaptor.capture());
        TrainerWorkload finalWorkload = testStorage.get(username);
        assertNotNull(finalWorkload);
        assertEquals(60, finalWorkload.getTotalDuration(2025, 9));
        assertEquals(90, finalWorkload.getTotalDuration(2026, 9));
        assertEquals(50, finalWorkload.getTotalDuration(2027, 9));
    }

    @Test
    void testShouldGetAllWorkloads() {
        TrainerWorkload workload1 = TrainerWorkload.builder().username("Dart.veit")
                .firstName("Dart").lastName("Veit").isActive(true).build();
        TrainerWorkload workload2 = TrainerWorkload.builder().username("Dart.veit2")
                .firstName("Dart").lastName("Veit").isActive(true).build();
        TrainerWorkload workload3 = TrainerWorkload.builder().username("Dart.veit3")
                .firstName("Dart").lastName("Veit").isActive(true).build();

        when(workloadRepository.findAll()).thenReturn(List.of(workload1, workload2, workload3));

        List<TrainerWorkload> allWorkloads = workloadService.getAllWorkloads();
        assertNotNull(allWorkloads);
        assertEquals(3, allWorkloads.size());
    }

    @Test
    void testShouldReturnNullForNonExistedTrainer() {
        TrainerWorkload workload = workloadService.getTrainerWorkload("non.existent");
        assertNull(workload);
    }

    @Test
    void testShouldUpdateTrainerInfo() {
        String username = "Timoty.Shnaider";
        WorkloadRequest request1 = WorkloadRequest.builder()
                .username(username)
                .firstName("Old")
                .lastName("Name")
                .isActive(true)
                .trainingDate(LocalDateTime.of(2026, 9, 10, 18, 00, 00))
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();

        WorkloadRequest request2 = WorkloadRequest.builder()
                .username(username)
                .firstName("New")
                .lastName("UpdatedName")
                .isActive(false)
                .trainingDate(LocalDateTime.of(2026, 9, 11, 18, 00, 00))
                .trainingDuration(30)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();

        workloadService.processWorkload(request1);
        workloadService.processWorkload(request2);

        verify(workloadRepository, times(2)).save(workloadCaptor.capture());
        TrainerWorkload finalWorkload = testStorage.get(username);

        assertEquals("New", finalWorkload.getFirstName());
        assertEquals("UpdatedName", finalWorkload.getLastName());
        assertFalse(finalWorkload.getIsActive());
        assertEquals(90, finalWorkload.getTotalDuration(2026, 9));
    }


    private WorkloadRequest createRequest(String username, LocalDateTime date, int duration,
                                          WorkloadRequest.ActionType actionType) {
        return WorkloadRequest.builder()
                .username(username)
                .firstName("First")
                .lastName("Last")
                .isActive(true)
                .trainingDate(date)
                .trainingDuration(duration)
                .actionType(actionType)
                .build();
    }
}
