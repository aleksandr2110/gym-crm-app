package epam.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "training_types")
@AllArgsConstructor
@NoArgsConstructor
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "training_type_name", nullable = true)
    @Enumerated(EnumType.STRING)
    private TrainingTypeName trainingTypeName;

    @OneToMany(mappedBy = "trainingType")
    private List<Training> trainings = new ArrayList<>();

    @OneToMany(mappedBy = "specialization")
    private List<Trainer> trainers = new ArrayList<>();

    public TrainingType(TrainingTypeName trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
