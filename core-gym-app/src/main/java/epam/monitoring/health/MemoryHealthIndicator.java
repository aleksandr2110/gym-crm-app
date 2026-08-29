package epam.monitoring.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MemoryHealthIndicator implements HealthIndicator {

    private static final double THRESHOLD_PERCENTAGE = 90.0;

    @Override
    public Health health() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            double usedPercentage = (double) usedMemory / maxMemory * 100;
            double freePercentage = 100 - usedPercentage;

            log.debug("Memory health check: {}% used, {}% free",
                    String.format("%.2f", usedPercentage),
                    String.format("%.2f", freePercentage));

            if (usedPercentage < THRESHOLD_PERCENTAGE) {
                return Health.up()
                        .withDetail("maxMemory", formatBytes(maxMemory))
                        .withDetail("totalMemory", formatBytes(totalMemory))
                        .withDetail("usedMemory", formatBytes(usedMemory))
                        .withDetail("freeMemory", formatBytes(freeMemory))
                        .withDetail("usedPercentage", String.format("%.2f%%", usedPercentage))
                        .withDetail("freePercentage", String.format("%.2f%%", freePercentage))
                        .withDetail("threshold", String.format("%.0f%%", THRESHOLD_PERCENTAGE))
                        .build();
            } else {
                log.warn("Memory usage is above threshold: {}%", String.format("%.2f", usedPercentage));
                return Health.down()
                        .withDetail("maxMemory", formatBytes(maxMemory))
                        .withDetail("usedMemory", formatBytes(usedMemory))
                        .withDetail("freeMemory", formatBytes(freeMemory))
                        .withDetail("usedPercentage", String.format("%.2f%%", usedPercentage))
                        .withDetail("threshold", String.format("%.0f%%", THRESHOLD_PERCENTAGE))
                        .withDetail("warning", "Memory usage is critically high")
                        .build();
            }
        } catch (Exception e) {
            log.error("Memory health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
