package epam.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadRequest implements Serializable {

    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private LocalDateTime trainingDate;
    private Integer trainingDuration;
    private ActionType actionType;

    public enum ActionType {
        ADD, DELETE
    }
}
