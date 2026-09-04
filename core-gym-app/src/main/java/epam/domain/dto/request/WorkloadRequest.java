package epam.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkloadRequest  { // implements Serializable

    @JsonProperty("username")
    private String username;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("isActive")
    private Boolean isActive;
    @JsonProperty("trainingDate")
    private LocalDateTime trainingDate;
    @JsonProperty("trainingDuration")
    private Integer trainingDuration;
    @JsonProperty("actionType")
    private String actionType;

    @JsonCreator
    public WorkloadRequest() { }

    @JsonCreator
    public WorkloadRequest(@JsonProperty("username") String username,  @JsonProperty("firstName") String firstName,
                           @JsonProperty("lastName") String lastName, @JsonProperty("isActive") Boolean isActive,
                           @JsonProperty("trainingDate") LocalDateTime trainingDate,
                           @JsonProperty("trainingDuration") Integer trainingDuration,
                           @JsonProperty("actionType") String actionType) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
        this.actionType = actionType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDateTime trainingDate) {
        this.trainingDate = trainingDate;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Integer getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Integer trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    /*@JsonCreator
    public WorkloadRequest(String username, String firstName, String lastName, Boolean isActive,
                           LocalDateTime trainingDate, Integer trainingDuration, ActionType actionType) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
        this.actionType = actionType;
    } */

//    @JsonValue
//    public ActionType getActionType() {
//        return actionType;
//    }

    //@AllArgsConstructor(onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.DELEGATING))
//    public class ActionType {
//
//        @JsonProperty("action")
//        String action;
//        public ActionType(String action) {
//            this.action = action;
//        }
//    }
    /*public enum ActionType { // class
        ADD, DELETE;

        @JsonCreator
        public static ActionType fromString(String value) {
            return ActionType.valueOf(value.toUpperCase());
        }
    }*/
}
