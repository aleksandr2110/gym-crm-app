package epam.client;

import epam.domain.dto.request.WorkloadRequest;

import epam.service.WorkloadService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {

    private final WorkloadServiceClient workloadServiceClient;

    @Override
    @Retry(name = "workloadService")
    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallbackUpdate")
    public void updateWorkload(WorkloadRequest request) {
        workloadServiceClient.updateWorkload(request);
    }

    private void fallbackUpdate(WorkloadRequest request, Throwable ex) {
        log.error("Workload service unavailable. Action: {}, Trainer: {}. Error: {}",
                request.getActionType(), request.getUsername(), ex.getMessage());
    }
}
