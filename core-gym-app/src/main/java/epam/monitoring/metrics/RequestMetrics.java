package epam.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RequestMetrics {

    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Timer requestDurationTimer;
    private final MeterRegistry meterRegistry;

    public RequestMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.requestCounter = Counter.builder("gym.requests.total")
                .description("Total number of HTTP requests")
                .tag("type", "http")
                .register(meterRegistry);

        this.errorCounter = Counter.builder("gym.requests.errors")
                .description("Total number of HTTP request errors")
                .tag("type", "http")
                .register(meterRegistry);

        this.requestDurationTimer = Timer.builder("gym.requests.duration")
                .description("HTTP request duration")
                .tag("type", "http")
                .register(meterRegistry);

        log.info("Request metrics initialized");
    }

    public void incrementRequestCount() {
        requestCounter.increment();
    }

    public void incrementErrorCount() {
        errorCounter.increment();
        log.debug("Error counter incremented");
    }

    public void recordRequestDuration(long durationMillis) {
        requestDurationTimer.record(durationMillis, TimeUnit.MILLISECONDS);
        log.debug("Request duration recorded: {}ms", durationMillis);
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(requestDurationTimer);
    }
}
