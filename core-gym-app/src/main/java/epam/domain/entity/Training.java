package epam.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "trainings")
@AllArgsConstructor
@NoArgsConstructor
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainee_id", nullable = true)
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = true)
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "training_type_id", nullable = true)
    private TrainingType trainingType;

    @Column(name = "training_name", nullable = true)
    private String trainingName;


    @Column(name = "training_date", nullable = true)
    private LocalDateTime trainingDate;

    @Column(name = "training_duration", nullable = true)
    private Integer trainingDuration;

    @PreRemove
    private void removeAssociations() {
        if (trainee != null && trainee.getTrainings() != null) {
            trainee.getTrainings().remove(this);
        }
        if (trainer != null && trainer.getTrainings() != null) {
            trainer.getTrainings().remove(this);
        }
    }

}
