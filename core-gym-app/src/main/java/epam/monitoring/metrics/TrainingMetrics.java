package epam.monitoring.metrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class TrainingMetrics {

    private final Counter trainingCreatedCounter;
    private final AtomicInteger activeTrainingsCount;

    public TrainingMetrics(MeterRegistry meterRegistry) {
        this.trainingCreatedCounter = Counter.builder("gym.trainings.created")
                .description("Total number of trainings created")
                .tag("type", "training")
                .register(meterRegistry);

        this.activeTrainingsCount = new AtomicInteger(0);

        Gauge.builder("gym.trainings.active", activeTrainingsCount, AtomicInteger::get)
                .description("Number of active trainings")
                .tag("type", "training")
                .register(meterRegistry);

        log.info("Training metrics initialized");
    }

    public void incrementTrainingCreated() {
        trainingCreatedCounter.increment();
        log.debug("Training created counter incremented");
    }

    public void incrementActiveTrainings() {
        activeTrainingsCount.incrementAndGet();
        log.debug("Active trainings count incremented to {}", activeTrainingsCount.get());
    }

    public void decrementActiveTrainings() {
        activeTrainingsCount.decrementAndGet();
        log.debug("Active trainings count decremented to {}", activeTrainingsCount.get());
    }

    public int getActiveTrainingsCount() {
        return activeTrainingsCount.get();
    }
}
