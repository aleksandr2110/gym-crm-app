package epam.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.cucumber.util.JwtTestUtil;
import epam.domain.dto.request.WorkloadRequest;
import epam.domain.model.TrainerWorkload;
import epam.repository.MongoWorkloadRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class WorkloadStepDefinition {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkloadTestContext context;

    @Autowired
    private MongoWorkloadRepository workloadRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void cleanDatabase() {
        workloadRepository.deleteAll();
    }

    private String authToken() {
        return "Bearer " + JwtTestUtil.generateToken("test-user");
    }

    @Given("No workload exists for trainer {string}")
    public void noWorkloadExistsForTrainer(String username) {
        workloadRepository.findByUsername(username).ifPresent(workloadRepository::delete);
    }

    @When("Send POST for a workload request for trainer {string} firstName {string} lastName {string} action {string} duration {int} date {string}")
    public void postWorkloadRequest(String username, String firstName, String lastName,
                                     String action, int duration, String date) throws Exception {
        WorkloadRequest request = WorkloadRequest.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .trainingDate(LocalDateTime.parse(date))
                .trainingDuration(duration)
                .actionType(WorkloadRequest.ActionType.valueOf(action))
                .build();

        MvcResult result = mockMvc.perform(
                post("/api/workload")
                        .header("Authorization", authToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn();

        context.setLastResult(result);
    }

    @Given("A workload exists for trainer {string} with {int} minutes in year {int} month {int}")
    public void workloadExistsForTrainer(String username, int minutes, int year, int month) {
        TrainerWorkload workload = workloadRepository.findByUsername(username)
                .orElse(TrainerWorkload.builder()
                        .username(username)
                        .firstName(username.split("\\.")[0])
                        .lastName(username.split("\\.")[1])
                        .isActive(true)
                        .build());
        workload.updateWorkload(year, month, minutes);
        workloadRepository.save(workload);
    }


    @Given("Workloads exist for trainers {string} and {string}")
    public void workloadsExistForTrainers(String username1, String username2) {
        workloadExistsForTrainer(username1, 60, 2026, 10);
        workloadExistsForTrainer(username2, 90, 2026, 11);
    }



    @When("Send post a workload request with missing username")
    public void postWorkloadRequestWithMissingUsername() throws Exception {
        String body = """
                {
                    "firstName": "Anna",
                    "lastName": "Lee",
                    "isActive": true,
                    "trainingDate": "2026-10-01",
                    "trainingDuration": 60,
                    "actionType": "ADD"
                }
                """;
        MvcResult result = mockMvc.perform(
                post("/api/workload")
                        .header("Authorization", authToken())
                        .contentType(APPLICATION_JSON)
                        .content(body)
        ).andReturn();
        context.setLastResult(result);
    }

    @When("Send post a workload request with missing firstName")
    public void postWorkloadRequestWithMissingFirstName() throws Exception {
        String body = """
                {
                    "username": "John.Doe",
                    "lastName": "Doe",
                    "isActive": true,
                    "trainingDate": "2026-11-01",
                    "trainingDuration": 60,
                    "actionType": "ADD"
                }
                """;
        MvcResult result = mockMvc.perform(
                post("/api/workload")
                        .header("Authorization", authToken())
                        .contentType(APPLICATION_JSON)
                        .content(body)
        ).andReturn();
        context.setLastResult(result);
    }

    @When("Send post an unauthenticated workload request for trainer {string}")
    public void postUnauthenticatedWorkloadRequest(String username) throws Exception {
        WorkloadRequest request = WorkloadRequest.builder()
                .username(username)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .trainingDate(LocalDateTime.of(2026, 11, 1, 18, 00, 00))
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();

        MvcResult result = mockMvc.perform(
                post("/api/workload")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn();

        context.setLastResult(result);
    }

    @When("Getting workload for trainer {string}")
    public void getWorkloadForTrainer(String username) throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/workload/{username}", username)
                        .header("Authorization", authToken())
        ).andReturn();
        context.setLastResult(result);
    }

    @When("Getting workload unauthenticated for trainer {string}")
    public void gettingWorkloadUnauthenticatedForTrainer(String username) throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/workload/{username}", username)
                        .accept(APPLICATION_JSON)
        ).andReturn();
        context.setLastResult(result);
    }

    @When("Getting all workloads")
    public void getAllWorkloads() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/workload")
                        .header("Authorization", authToken())
        ).andReturn();
        context.setLastResult(result);
    }

    @When("Getting all workloads unauthenticated")
    public void GetAllWorkloadsUnauthenticated() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/workload")).andReturn();
        context.setLastResult(result);
    }

    @Then("The response status is {int}")
    public void theResponseStatusIs(int expectedStatus) throws Exception {
        assertThat(context.getLastStatus()).isEqualTo(expectedStatus);
    }

    @Then("The workload for trainer {string} in year {int} month {int} is {int} minutes")
    public void theWorkloadForTrainerInYearMonthIsMinutes(String username, int year, int month, int expectedMinutes) {
        TrainerWorkload workload = workloadRepository.findByUsername(username).orElseThrow(
                () -> new AssertionError("Workload not found for trainer: " + username)
        );
        assertThat(workload.getTotalDuration(year, month)).isEqualTo(expectedMinutes);
    }

    @Then("The response contains trainer username {string}")
    public void theResponseContainsTrainerUsername(String username) throws Exception {
        String body = context.getLastResponseBody();
        assertThat(body).contains(username);
    }

    @Then("The response is a JSON array with at least {int} entries")
    public void theResponseIsAJsonArrayWithAtLeastEntries(int minCount) throws Exception {
        String body = context.getLastResponseBody();
        assertThat(body.trim()).startsWith("[");
        long count = body.chars().filter(c -> c == '{').count();
        assertThat(count).isGreaterThanOrEqualTo(minCount);
    }
}
