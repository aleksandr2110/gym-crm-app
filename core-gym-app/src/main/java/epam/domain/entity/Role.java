package epam.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
public class Role  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 70)
    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

}
