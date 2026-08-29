package epam.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "trainees")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@PrimaryKeyJoinColumn(name = "id")
public class Trainee extends User {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "date_birth", nullable = true)
    private LocalDate dateOfBirth;

    @Column(name = "address", nullable = true)
    private String address;

    @ManyToMany(mappedBy = "trainees")
    private List<Trainer> trainers = new ArrayList<>();

    @PreRemove
    public void removeAssociations() {
        for (Trainer trainer : this.getTrainers()) {
            trainer.getTrainees().remove(this);
        }
        trainers.clear();

        System.out.println("remove delete association ");
        for (Training training : this.getTrainings()) {
            training.setTrainee(null);
        }
        trainings.clear();
    }

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    public void addTraining(Training training) {
        trainings.add(training);
    }

    @Override
    public String toString() {
        return "Trainee{" +
                "id=" + id +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", trainers=" + trainers +
                ", trainings=" + trainings +
                '}';
    }
}
