package epam.domain.repo;

import epam.domain.model.TrainerWorkload;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WorkloadRepository {

    TrainerWorkload save(TrainerWorkload workload);

    Optional<TrainerWorkload> findByUsername(String username);

    List<TrainerWorkload> findAll();
}
