package epam.cucumber.component.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AuthStep {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("login with username {string} and password {string}")
    public void loginWithUsernameAndPassword(String username, String password) throws Exception {
        String body = objectMapper.writeValueAsString(new LoginBody(username, password));

        MvcResult result = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        ).andReturn();

        context.setLastResult(result);
    }


    @Then("the response contains a JWT token")
    public void theResponseContainsAJwtToken() throws Exception {
        String body = context.getLastResponseBody();
        assertThat(body).contains("token");
        JsonNode json = objectMapper.readTree(body);
        assertThat(json.has("token")).isTrue();
        assertThat(json.get("token").asText()).isNotBlank();
    }

    record LoginBody(String username, String password) {}
}
