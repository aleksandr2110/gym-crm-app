package epam.monitoring.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class DiskSpaceHealthIndicator implements HealthIndicator {

    private static final long THRESHOLD_BYTES = 10 * 1024 * 1024 * 1024L; //10GB

    @Override
    public Health health() {
        try {
            File root = new File("/");
            long freeSpace = root.getFreeSpace();
            long totalSpace = root.getTotalSpace();
            long usableSpace = root.getUsableSpace();

            double freePercentage = (double) freeSpace / totalSpace * 100;

            log.debug("Disk space health check: {}% free", String.format("%.2f", freePercentage));

            if (freeSpace >= THRESHOLD_BYTES) {
                return Health.up()
                        .withDetail("total", formatBytes(totalSpace))
                        .withDetail("free", formatBytes(freeSpace))
                        .withDetail("usable", formatBytes(usableSpace))
                        .withDetail("freePercentage", String.format("%.2f%%", freePercentage))
                        .withDetail("threshold", formatBytes(THRESHOLD_BYTES))
                        .build();
            } else {
                return Health.down()
                        .withDetail("total", formatBytes(totalSpace))
                        .withDetail("free", formatBytes(freeSpace))
                        .withDetail("usable", formatBytes(usableSpace))
                        .withDetail("freePercentage", String.format("%.2f%%", freePercentage))
                        .withDetail("threshold", formatBytes(THRESHOLD_BYTES))
                        .withDetail("error", "Free disk space is below threshold")
                        .build();
            }
        } catch (Exception e) {
            log.error("Disk space health check failed", e);
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
