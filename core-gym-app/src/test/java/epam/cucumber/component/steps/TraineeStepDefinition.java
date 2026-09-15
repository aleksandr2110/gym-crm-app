package epam.cucumber.component.steps;
import com.fasterxml.jackson.databind.ObjectMapper;

import epam.domain.dto.request.TraineeRequestDTO;
import epam.domain.dto.request.UpdateTraineeTrainersRequestDTO;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TraineeStepDefinition {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("Registration a trainee with firstName {string} lastName {string} dateOfBirth {string} address {string}")
    public void registerTrainee(String firstName, String lastName, String dateOfBirth, String address) throws Exception {
        TraineeRequestDTO request = new TraineeRequestDTO();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        if (dateOfBirth != null && !dateOfBirth.isBlank()) {
            request.setDateOfBirth(LocalDate.parse(dateOfBirth));
        }
        request.setAddress(address);

        MvcResult result = mockMvc.perform(
                post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn();

        context.setLastResult(result);
    }

    @Then("the response contains a username {string}")
    public void theResponseContainsUsername(String expectedUsername) throws Exception {
        assertThat(context.getLastResponseBody()).contains(expectedUsername);
    }

    @Then("the response contains a generated password")
    public void theResponseContainsAGeneratedPassword() throws Exception {
        assertThat(context.getLastResponseBody()).contains("password");
    }

    @When("Using patch end-point {string} with param {string} {string} and param {string} {string}")
    public void patchingWithParams(String path, String param1, String value1,
                                   String param2, String value2) throws Exception {
        MockHttpServletRequestBuilder builder = patch(path)
                .param(param1, value1)
                .param(param2, value2);
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

    @When("Updating trainee {string} trainers list with trainer usernames {string}")
    public void updatingTrainersList(String trainee, String trainerUsernames) throws Exception {
        List<String> usernames = Arrays.asList(trainerUsernames.split(","));
        UpdateTraineeTrainersRequestDTO request = new UpdateTraineeTrainersRequestDTO();
        request.setTrainerUsernames(usernames);
        request.setTraineeUsername(trainee);

        MockHttpServletRequestBuilder builder = put("/api/v1/trainees/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

}
