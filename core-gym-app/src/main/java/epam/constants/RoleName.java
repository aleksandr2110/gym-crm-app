package epam.constants;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum RoleName {

    ROLE_USER(Set.of(Permission.USER_READ)),
    ROLE_ADMIN(Set.of(Permission.USER_READ, Permission.USER_WRITE, Permission.ADMIN_READ, Permission.ADMIN_WRITE)),
    ROLE_TRAINER(Set.of(Permission.USER_READ, Permission.USER_WRITE)),
    ROLE_TRAINEE(Set.of(Permission.USER_READ, Permission.USER_WRITE));


    private final Set<Permission> permissions;

    RoleName(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return authorities;
    }
}
