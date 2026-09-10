package epam.repository;

import epam.domain.model.TrainerWorkload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MongoWorkloadRepositoryBridgeTest {

    @Mock
    private MongoWorkloadRepository mongoRepository;

    @InjectMocks
    private MongoWorkloadRepositoryBridge bridge;

    @Test
    void testShouldSaveWorkload() {
        TrainerWorkload workload = TrainerWorkload.builder()
                .username("Nataly.klain")
                .firstName("Nataly")
                .lastName("Klain")
                .isActive(true)
                .build();

        when(mongoRepository.save(workload)).thenReturn(workload);

        TrainerWorkload saved = bridge.save(workload);

        assertNotNull(saved);
        assertEquals("Nataly.klain", saved.getUsername());
        verify(mongoRepository).save(workload);
    }

    @Test
    void testShouldFindWorkloadByUsername() {
        TrainerWorkload workload = TrainerWorkload.builder()
                .username("David.gosling")
                .firstName("David")
                .lastName("Gosling")
                .isActive(true)
                .build();

        when(mongoRepository.findByUsername("David.gosling")).thenReturn(Optional.of(workload));

        Optional<TrainerWorkload> found = bridge.findByUsername("David.gosling");

        assertTrue(found.isPresent());
        assertEquals("David.gosling", found.get().getUsername());
        verify(mongoRepository).findByUsername("David.gosling");
    }

    @Test
    void testShouldFindByUsernameWhenNotFound() {
        when(mongoRepository.findByUsername("incognito")).thenReturn(Optional.empty());

        Optional<TrainerWorkload> found = bridge.findByUsername("incognito");

        assertTrue(found.isEmpty());
        verify(mongoRepository).findByUsername("incognito");
    }

    @Test
    void testShouldFindAllWorkloads() {
        TrainerWorkload w1 = TrainerWorkload.builder().username("username1").firstName("Alex")
                .lastName("Hofman").isActive(true).build();
        TrainerWorkload w2 = TrainerWorkload.builder().username("username2").firstName("Antony")
                .lastName("Richard").isActive(true).build();

        when(mongoRepository.findAll()).thenReturn(List.of(w1, w2));

        List<TrainerWorkload> all = bridge.findAll();

        assertEquals(2, all.size());
        verify(mongoRepository).findAll();
    }
}
