package epam.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Schema(description = "Request payload for updating trainer workload")
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkloadRequest implements Serializable { // implements Serializable

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

    /*@JsonValue
    public ActionType getActionType() {
        return actionType;
    }
    @AllArgsConstructor(onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.DELEGATING))
    public enum ActionType {
        ADD, DELETE;

        @JsonCreator
        public static ActionType fromString(String value) {
            return ActionType.valueOf(value.toUpperCase());
        }
    } */
    /*@Schema(description = "Trainer's unique username", example = "Jeremi.Mann")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Trainer's first name", example = "Jeremi")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Trainer's last name", example = "Mann")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "Trainer's active status", example = "true")
    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @Schema(description = "Training date", example = "2026-08-21-18-00-00")
    @NotNull(message = "Training date is required")
    private LocalDateTime trainingDate;

    @Schema(description = "Training duration in minutes", example = "60")
    @NotNull(message = "Training duration is required")
    @Positive(message = "Training duration must be positive")
    private Integer trainingDuration;

    @Schema(description = "Action type: ADD (add training) or DELETE (remove training)", example = "ADD")
    @NotNull(message = "Action type is required")
    private ActionType actionType;

    @Schema(description = "Action type enumeration")
    public enum ActionType {
        @Schema(description = "Add training session")
        ADD,
        @Schema(description = "Delete training session")
        DELETE;
        @JsonCreator
        public static ActionType fromString(String value) {
            return ActionType.valueOf(value.toUpperCase());
        }
    } */

}
