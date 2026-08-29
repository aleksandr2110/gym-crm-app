package epam.service;

import epam.domain.dto.request.WorkloadRequest;

public interface WorkloadService {

    void updateWorkload(WorkloadRequest request);
}
