package epam.cucumber.component.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class CommonStepDefinition {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestContext context;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    @Transactional
    public void cleanUpDatabase() {
        context.clearAuthentication();
    }

    @Given("Authentication as {string} with role {string}")
    public void AuthenticationWithRole(String username, String role) {
        context.setAuthentication(username, role);
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int expectedStatus) throws Exception {
        assertThat(context.getLastStatus()).isEqualTo(expectedStatus);
    }

    @Then("the response is a JSON array")
    public void theResponseIsAJsonArray() throws Exception {
        String body = context.getLastResponseBody();
        assertThat(body.trim()).startsWith("[");
    }

    @Then("the response contains field {string} with value {string}")
    public void theResponseContainsFieldWithValue(String field, String value) throws Exception {
        String body = context.getLastResponseBody();
        assertThat(body).contains("\"" + field + "\"").contains(value);
    }

    @When("Getting {string}")
    public void getting(String path) throws Exception {
        MockHttpServletRequestBuilder builder = get(path);
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

    @When("Deleting {string}")
    public void deleting(String path) throws Exception {
        MockHttpServletRequestBuilder builder = delete(path);
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
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
}
