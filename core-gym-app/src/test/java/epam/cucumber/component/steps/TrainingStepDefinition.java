package epam.cucumber.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.domain.dto.request.TraineeTrainingsRequestDTO;
import epam.domain.dto.request.TrainerTrainingsRequestDTO;
import epam.domain.dto.request.TrainingRequestDTO;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainingStepDefinition {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("Creating a training with trainee {string} trainer {string} name {string} type {string} date {string} duration {int}")
    public void creatingTraining(String traineeUsername, String trainerUsername, String name,
                                String type, String date, int duration) throws Exception {
        TrainingRequestDTO request = new TrainingRequestDTO();
        request.setTraineeUsername(traineeUsername);
        request.setTrainerUsername(trainerUsername);
        request.setTrainingName(name);
        request.setTrainingType(type);
        request.setTrainingDate(date);
        request.setTrainingDuration(duration);

        MockHttpServletRequestBuilder builder = post("/api/v1/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

    @When("Getting a trainee trainings for username {string} trainer {string} period from {string} to {string} specialization {string}")
    public void getTraineeTrainings(String username, String trainerUsername, String periodFrom,
                                    String periodTo, String specialization) throws Exception {
        var request = new TraineeTrainingsRequestDTO();
        request.setUsername(username);
        request.setTrainerName(trainerUsername);
        request.setPeriodFrom(periodFrom);
        request.setPeriodTo(periodTo);
        request.setTrainingType(specialization);

        MockHttpServletRequestBuilder builder = get("/api/v1/trainings/trainee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

    @When("Getting a trainer trainings for username {string} trainee {string} period from {string} to {string}")
    public void getTrainerTrainings(String username, String traineeUsername, String periodFrom, String periodTo) throws Exception {
        var request = new TrainerTrainingsRequestDTO();
        request.setUsername(username);
        request.setTraineeName(traineeUsername);
        request.setPeriodFrom(periodFrom);
        request.setPeriodTo(periodTo);

        MockHttpServletRequestBuilder builder = get("/api/v1/trainings/trainer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }
}
