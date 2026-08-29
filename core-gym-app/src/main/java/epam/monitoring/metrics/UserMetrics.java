package epam.monitoring.metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class UserMetrics {

    private final Counter userRegistrationCounter;
    private final Counter userLoginCounter;
    private final AtomicInteger activeUsersCount;

    public UserMetrics(MeterRegistry meterRegistry) {
        this.userRegistrationCounter = Counter.builder("gym.users.registered")
                .description("Total number of users registered")
                .tag("type", "user")
                .register(meterRegistry);

        this.userLoginCounter = Counter.builder("gym.users.logins")
                .description("Total number of user logins")
                .tag("type", "user")
                .register(meterRegistry);

        this.activeUsersCount = new AtomicInteger(0);

        Gauge.builder("gym.users.active", activeUsersCount, AtomicInteger::get)
                .description("Number of currently active users")
                .tag("type", "user")
                .register(meterRegistry);

        log.info("User metrics initialized");
    }

    public void incrementUserRegistration() {
        userRegistrationCounter.increment();
        log.debug("User registration counter incremented");
    }

    public void incrementUserLogin() {
        userLoginCounter.increment();
        activeUsersCount.incrementAndGet();
        log.debug("User login counter incremented, active users: {}", activeUsersCount.get());
    }

    public void decrementActiveUsers() {
        if (activeUsersCount.get() > 0) {
            activeUsersCount.decrementAndGet();
            log.debug("Active users count decremented to {}", activeUsersCount.get());
        }
    }

    public int getActiveUsersCount() {
        return activeUsersCount.get();
    }

}
