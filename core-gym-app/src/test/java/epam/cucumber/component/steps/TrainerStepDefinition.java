package epam.cucumber.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.domain.dto.request.TrainerRequestDTO;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainerStepDefinition {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("Registration a trainer with firstName {string} lastName {string} specialization {string}")
    public void registerTrainer(String firstName, String lastName, String specialization) throws Exception {
        TrainerRequestDTO request = new TrainerRequestDTO();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setSpecialization(specialization);

        MvcResult result = mockMvc.perform(
                post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn();

        context.setLastResult(result);
    }
}
