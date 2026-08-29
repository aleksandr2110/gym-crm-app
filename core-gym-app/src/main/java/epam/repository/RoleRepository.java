package epam.repository;

import epam.domain.entity.Role;
import epam.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class RoleRepository {

    private final EntityManager entityManager;

    public RoleRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Role> findByName(String name) {
        TypedQuery<Role> query = entityManager.createQuery(
                "FROM Role r WHERE r.name = :name", Role.class);
        query.setParameter("name", name);
        return query.getResultStream().findAny();
    }
}
