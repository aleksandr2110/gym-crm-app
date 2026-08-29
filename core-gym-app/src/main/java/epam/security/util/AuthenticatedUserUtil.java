package epam.security.util;

import epam.domain.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserUtil {

    public boolean isProfileOwner(Long ownerId, String username) {
        if (ownerId == null || username == null) {
            return false;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return ownerId.equals(user.getId());
        }
        return false;
    }

    public boolean isProfileOwnerByUsername(String requestUsername, String authUsername) {
        if (requestUsername != null && authUsername != null) {
            return requestUsername.equals(authUsername);
        }
        return false;
    }
}
