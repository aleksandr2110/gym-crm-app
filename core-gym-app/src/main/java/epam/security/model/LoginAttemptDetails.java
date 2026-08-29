package epam.security.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptDetails {

    private int attempts;
    private LocalDateTime blockedDateTime;

    public LoginAttemptDetails(int attempts) {
        this.attempts = attempts;
        this.blockedDateTime = null;
    }

    public boolean isBlocked() {
        if (blockedDateTime == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(blockedDateTime.plusMinutes(5));
    }

    public long getRemainingBlockTimeInSeconds() {
        if (blockedDateTime == null) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime unblockTime = blockedDateTime.plusMinutes(5);

        if (now.isBefore(unblockTime)) {
            return java.time.Duration.between(now, unblockTime).getSeconds();
        }
        return 0;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public void block() {
        this.blockedDateTime = LocalDateTime.now();
    }
}
