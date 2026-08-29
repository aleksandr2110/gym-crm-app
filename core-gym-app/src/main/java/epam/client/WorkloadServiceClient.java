package epam.client;

import epam.domain.dto.request.WorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "workload-service")
public interface WorkloadServiceClient {

    @PostMapping("/api/workload")
    void updateWorkload(@RequestBody WorkloadRequest request);


}
