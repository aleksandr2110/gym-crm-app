package epam.monitoring.health;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Health health() {
        try {
            Long count = entityManager.createQuery("SELECT COUNT(t) FROM TrainingType t", Long.class)
                    .getSingleResult();

            log.debug("Database health check: {} training types found", count);

            return Health.up()
                    .withDetail("database", "MySQL")
                    .withDetail("status", "Connection successful")
                    .withDetail("trainingTypesCount", count)
                    .build();
        } catch (Exception e) {
            log.error("Database health check failed", e);
            return Health.down()
                    .withDetail("database", "MySQL")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
