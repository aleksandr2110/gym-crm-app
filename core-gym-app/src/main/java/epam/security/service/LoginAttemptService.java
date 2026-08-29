package epam.security.service;

import epam.security.model.LoginAttemptDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private final Map<String, LoginAttemptDetails> loginAttempts = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        log.info("Login succeeded for user: {}, clearing failed attempts", username);
        loginAttempts.remove(username);
    }

    public void loginFailed(String username) {
        LoginAttemptDetails details = loginAttempts.computeIfAbsent(
                username, k -> new LoginAttemptDetails(0));
        details.incrementAttempts();
        log.warn("Login failed for user: {}, attempts: {}", username, details.getAttempts());

        if (details.getAttempts() >= MAX_ATTEMPTS) {
            details.block();
            log.warn("User {} has been blocked according to {} failed login attempts", username,
                    details.getAttempts());
        }
    }

    public boolean isBlocked(String username) {
        LoginAttemptDetails details = loginAttempts.get(username);
        if (details == null) {
            return false;
        }

        boolean blocked = details.isBlocked();
        if (!blocked && details.getBlockedDateTime() != null) {
            log.info("Block time expired for user: {}, removing from map", username);
            loginAttempts.remove(username);
        }

        if (blocked) {
            log.warn("User {} is currently blocked", username);
        }

        return blocked;
    }

    public long getRemainingBlockTimeInSeconds(String username) {
        LoginAttemptDetails details = loginAttempts.get(username);
        if (details == null) {
            return 0;
        }
        return details.getRemainingBlockTimeInSeconds();
    }
}
