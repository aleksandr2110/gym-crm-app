package epam.messaging;

import epam.domain.dto.request.WorkloadRequest;
import epam.service.WorkloadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class WorkloadMessageConsumerTest {

    @Mock
    private WorkloadService workloadService;

    @InjectMocks
    private WorkloadMessageConsumer consumer;

    @Test
    void testShouldHandleValidWorkloadMessage() {
        WorkloadRequest request = buildValidRequest();

        assertDoesNotThrow(() -> consumer.receiveWorkloadMessage(request, "transaction-1"));

        verify(workloadService).processWorkload(request);
    }

    @Test
    void testShouldHandleValidWorkloadMessageWithoutTransactionId() {
        WorkloadRequest request = buildValidRequest();

        assertDoesNotThrow(() -> consumer.receiveWorkloadMessage(request, null));

        verify(workloadService).processWorkload(request);
    }

    @Test
    void testShouldRejectWorkloadMessageWithoutUsername() {
        WorkloadRequest request = buildValidRequest();
        request.setUsername(null);

        assertThrows(IllegalArgumentException.class,
                () -> consumer.receiveWorkloadMessage(request, "transaction-1"));

        verifyNoInteractions(workloadService);
    }

    @Test
    void testReceiveWorkloadMessageWithoutEmptyUsername() {
        WorkloadRequest request = buildValidRequest();
        request.setUsername("  ");

        assertThrows(IllegalArgumentException.class,
                () -> consumer.receiveWorkloadMessage(request, "transaction-1"));

        verifyNoInteractions(workloadService);
    }

    @Test
    void testReceiveWorkloadMessageWithoutTrainingDate() {
        WorkloadRequest request = buildValidRequest();
        request.setTrainingDate(null);

        assertThrows(IllegalArgumentException.class,
                () -> consumer.receiveWorkloadMessage(request, "transaction-1"));

        verifyNoInteractions(workloadService);
    }

    @Test
    void testReceiveWorkloadMessageWithoutActionType() {
        WorkloadRequest request = buildValidRequest();
        request.setActionType(null);

        assertThrows(IllegalArgumentException.class,
                () -> consumer.receiveWorkloadMessage(request, "transaction-1"));

        verifyNoInteractions(workloadService);
    }

    @Test
    void  testReceiveWorkloadMessageWithoutTrainingDuration() {
        WorkloadRequest request = buildValidRequest();
        request.setTrainingDuration(0);

        assertThrows(IllegalArgumentException.class,
                () -> consumer.receiveWorkloadMessage(request, "transaction-1"));

        verifyNoInteractions(workloadService);
    }

    private WorkloadRequest buildValidRequest() {
        return WorkloadRequest.builder()
                .username("jeff.ex")
                .firstName("Jeff")
                .lastName("Ex")
                .isActive(true)
                .trainingDate(LocalDateTime.of(2026, 9, 15, 18, 00, 00))
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();
    }

}
