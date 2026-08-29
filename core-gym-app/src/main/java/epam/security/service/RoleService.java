package epam.security.service;


import epam.constants.RoleName;
import epam.domain.entity.Role;
import epam.domain.entity.Trainee;
import epam.domain.entity.Trainer;
import epam.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public void assignRoleToTrainee(Trainee trainee, RoleName roleName) {
        log.debug("Assigning role {} to trainee {}", roleName, trainee.getUsername());

        Role role = roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        trainee.getRoles().add(role);
        log.debug("Role {} assigned to trainee {}", roleName, trainee.getUsername());
    }

    @Transactional
    public void assignRoleToTrainer(Trainer trainer, RoleName roleName) {
        log.debug("Assigning role {} to trainer {}", roleName, trainer.getUsername());

        Role role = roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        trainer.getRoles().add(role);
        log.debug("Role {} assigned to trainer {}", roleName, trainer.getUsername());
    }
}
