package epam.repository;

import epam.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class UserRepository {

    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<User> findByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        return query.getResultStream().findAny();
    }
}
