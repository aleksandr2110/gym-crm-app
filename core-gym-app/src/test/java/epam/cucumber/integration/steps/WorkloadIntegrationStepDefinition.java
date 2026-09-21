package epam.cucumber.integration.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.domain.dto.request.TrainingRequestDTO;
import epam.domain.dto.request.WorkloadRequest;
import epam.repository.TrainingRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static epam.config.RabbitMQConfig.WORKLOAD_EXCHANGE;
import static epam.config.RabbitMQConfig.WORKLOAD_ROUTING_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class WorkloadIntegrationStepDefinition {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private IntegrationTestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    @Transactional
    public void cleanUp() {
        /*entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers_trainees").executeUpdate();
        entityManager.createNativeQuery(
                "DELETE FROM trainees WHERE user_id NOT IN " +
                        "(SELECT id FROM users WHERE username IN ('Alice.Brown', 'Bob.Wilson', 'Charlie.Davis'))"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "DELETE FROM trainers WHERE user_id NOT IN " +
                        "(SELECT id FROM users WHERE username IN ('John.Doe', 'Jane.Smith', 'Mike.Johnson', 'Sarah.Connor'))"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "DELETE FROM users WHERE username NOT IN " +
                        "('John.Doe', 'Jane.Smith', 'Mike.Johnson', 'Alice.Brown', 'Bob.Wilson', 'Charlie.Davis', 'Sarah.Connor')"
        ).executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 100").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE trainings ALTER COLUMN id RESTART WITH 100").executeUpdate();
        entityManager.flush();*/
        context.clearAuthentication();
        reset(rabbitTemplate);
    }

    @Given("Authentication as {string} with role {string}")
    public void authenticatedAsWithRole(String username, String role) {
        context.setAuthentication(username, role);
    }

    @When("Creating a training with trainee {string} trainer {string} name {string} type {string} date {string} duration {int}")
    public void createTraining(String traineeUsername, String trainerUsername, String name,
                                String type, String date, int duration) throws Exception {
        var request = new TrainingRequestDTO();
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

    @When("Deleting {string}")
    public void deleteTraining(String path) throws Exception {
        MockHttpServletRequestBuilder builder = delete(path);
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int expectedStatus) throws Exception {
        assertThat(context.getLastStatus()).isEqualTo(expectedStatus);
    }

    @Then("a workload ADD message was sent for trainer {string} with duration {int}")
    public void WorkloadAddMessageWasSentForTrainer(String trainerUsername, int duration) {
        ArgumentCaptor<WorkloadRequest> captor = ArgumentCaptor.forClass(WorkloadRequest.class);
        verify(rabbitTemplate, atLeastOnce()).convertAndSend(
                eq(WORKLOAD_EXCHANGE), eq(WORKLOAD_ROUTING_KEY), captor.capture(), any(MessagePostProcessor.class)
        );
        List<WorkloadRequest> sent = captor.getAllValues();
        assertThat(sent).anyMatch(r ->
                trainerUsername.equals(r.getUsername()) &&
                        r.getActionType() == WorkloadRequest.ActionType.ADD &&
                        duration == r.getTrainingDuration()
        );
    }

    @Then("no workload message was sent")
    public void noWorkloadMessageWasSent() {
        verify(rabbitTemplate, never()).convertAndSend(
                eq(WORKLOAD_EXCHANGE), eq(WORKLOAD_ROUTING_KEY), any(WorkloadRequest.class), any(MessagePostProcessor.class)
        );
    }

    @Then("at least one workload DELETE message was sent")
    public void atLeastOneWorkloadDeleteMessageWasSent() {
        ArgumentCaptor<WorkloadRequest> captor = ArgumentCaptor.forClass(WorkloadRequest.class);
        verify(rabbitTemplate, atLeastOnce()).convertAndSend(
                eq(WORKLOAD_EXCHANGE), eq(WORKLOAD_ROUTING_KEY), captor.capture(), any(MessagePostProcessor.class)
        );
        List<WorkloadRequest> sent = captor.getAllValues();
        assertThat(sent).anyMatch(r -> r.getActionType() == WorkloadRequest.ActionType.DELETE);
    }

    @Then("{int} workload ADD messages were sent for trainer {string}")
    public void nWorkloadAddMessagesWereSentForTrainer(int count, String trainerUsername) {
        ArgumentCaptor<WorkloadRequest> captor = ArgumentCaptor.forClass(WorkloadRequest.class);
        verify(rabbitTemplate, times(count)).convertAndSend(
                eq(WORKLOAD_EXCHANGE), eq(WORKLOAD_ROUTING_KEY), captor.capture(), any(MessagePostProcessor.class)
        );
        long addCount = captor.getAllValues().stream()
                .filter(r -> trainerUsername.equals(r.getUsername()) &&
                        r.getActionType() == WorkloadRequest.ActionType.ADD)
                .count();
        assertThat(addCount).isEqualTo(count);
    }

    @Given("Resetting workload message tracking")
    public void resetWorkloadMessageTracking() {
        reset(rabbitTemplate);
    }

    @Given("RabbitMQ is configured to throw an exception")
    public void rabbitMqIsConfiguredToThrowAnException() {
        doThrow(new AmqpException("RabbitMQ unavailable"))
                .when(rabbitTemplate).convertAndSend(
                        eq(WORKLOAD_EXCHANGE), eq(WORKLOAD_ROUTING_KEY), any(WorkloadRequest.class), any(MessagePostProcessor.class)
                );
    }

    @Then("the training {string} exists in the database")
    public void theTrainingExistsInTheDatabase(String trainingName) {
        boolean exists = trainingRepository.findAll().stream()
                .anyMatch(t -> trainingName.equals(t.getTrainingName()));
        assertThat(exists).isTrue();
    }
}
