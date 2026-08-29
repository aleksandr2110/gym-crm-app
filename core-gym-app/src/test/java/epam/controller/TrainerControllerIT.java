package epam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.controller.exception.ExceptionHandlerController;
import epam.controller.rest.TrainerControllerImpl;
import epam.domain.dto.request.ChangePasswordRequestDTO;
import epam.domain.dto.request.TrainerRequestDTO;
import epam.domain.dto.response.RegistrationResponseDTO;
import epam.service.TrainerService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@WebAppConfiguration
@Transactional
public class TrainerControllerIT {

    @Autowired
    private TrainerControllerImpl trainerControllerImpl;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainerControllerImpl)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        entityManager.createNativeQuery("DELETE FROM trainers").executeUpdate();
    }

    @Test
    void testShouldCreateTrainerAndReturnCredentials() throws Exception {
        var request = new TrainerRequestDTO();
        request.setFirstName("Jastin");
        request.setLastName("Trudo");
        request.setSpecialization("JavaScript");

        MvcResult result = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Jastin.Trudo"))
                .andExpect(jsonPath("$.password").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseBody, RegistrationResponseDTO.class);

        assertNotNull(response.getUsername());
        assertNotNull(response.getPassword());
        assertEquals("Jastin.Trudo", response.getUsername());
        assertEquals(10, response.getPassword().length());
    }

    @Test
    void testShouldGenerateUniqueUsernameWhenDuplicateName() throws Exception {
        var request = new TrainerRequestDTO();
        request.setFirstName("Jastin");
        request.setLastName("Trudo");
        request.setSpecialization("JavaScript");

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Jastin.Trudo"));

        var request2 = new TrainerRequestDTO();
        request2.setFirstName("Jastin");
        request2.setLastName("Trudo");
        request2.setSpecialization("Java");

        MvcResult result = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Jastin.Trudo1"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseBody,
                RegistrationResponseDTO.class);

        assertEquals("Jastin.Trudo1", response.getUsername());
    }

    @Test
    void testShouldChangePasswordWithValidCredentials() throws Exception {
        var request = new TrainerRequestDTO();
        request.setFirstName("Jastin");
        request.setLastName("Trudo");
        request.setSpecialization("JavaScript");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseBody, RegistrationResponseDTO.class);
        String username = response.getUsername();
        String oldPassword = response.getPassword();

        var changePasswordRequest = new ChangePasswordRequestDTO();
        changePasswordRequest.setUsername(username);
        changePasswordRequest.setOldPassword(oldPassword);
        changePasswordRequest.setNewPassword("newPassword");

        mockMvc.perform(put("/api/v1/trainers/change-password")
                        .header("X-Username", username)
                        .header("X-Password", oldPassword)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());

        var trainer = trainerService.findByUsername(username);
        assertNotNull(trainer);
        assertEquals("newPassword", trainer.getPassword());
    }

    @Test
    void testShouldReturnErrorWhenChangePassword() throws Exception {
        var request = new TrainerRequestDTO();
        request.setFirstName("Jastin");
        request.setLastName("Trudo");
        request.setSpecialization("JavaScript");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseBody, RegistrationResponseDTO.class);

        String username = response.getUsername();
        String oldPassword = response.getPassword();

        var changePasswordRequest = new ChangePasswordRequestDTO();
        changePasswordRequest.setUsername(username);
        changePasswordRequest.setOldPassword("wrongPassword");
        changePasswordRequest.setNewPassword("newPassword789");

        mockMvc.perform(put("/api/v1/trainers/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testShouldReturnErrorWithNotExistedUser() throws Exception {
        var changePasswordRequest = new ChangePasswordRequestDTO();
        changePasswordRequest.setUsername("NonExistent.Trainer");
        changePasswordRequest.setOldPassword("somePassword");
        changePasswordRequest.setNewPassword("newPassword999");

        mockMvc.perform(put("/api/v1/trainers/change-password")
                        .header("X-Username", "NotExisted")
                        .header("X-Password", "somePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testShouldGetTraineeProfile() throws Exception {
        var request = new TrainerRequestDTO();
        request.setFirstName("Jastin");
        request.setLastName("Trudo");
        request.setSpecialization("JavaScript");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseBody, RegistrationResponseDTO.class);
        String username = response.getUsername();

        mockMvc.perform(get("/api/v1/trainers/" + username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("Jastin"))
                .andExpect(jsonPath("$.lastName").value("Trudo"))
                .andExpect(jsonPath("$.specialization").value("Javascript"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees").isArray());
    }

    @Test
    void testShouldGetErrorWithNotExistedUsername() throws Exception {
        mockMvc.perform(get("/api/v1/trainers/NonExistent.Trainer"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "Sarah.Connor", roles = {"ADMIN"})
    void testShouldActivateTrainer() throws Exception {
        var request = new TrainerRequestDTO();
        request.setFirstName("Jastin");
        request.setLastName("Trudo");
        request.setSpecialization("JavaScript");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseBody, RegistrationResponseDTO.class);
        String username = response.getUsername();
        String password = response.getPassword();

        mockMvc.perform(patch("/api/v1/trainers/activation")
                        .header("X-Username", username)
                        .header("X-Password", password)
                        .param("username", username)
                        .param("isActive", "true"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/trainers/activation")
                        .header("X-Username", username)
                        .header("X-Password", password)
                        .param("username", username)
                        .param("isActive", "true"))
                .andExpect(status().isOk());

        var trainer = trainerService.findByUsername(username);
        assertNotNull(trainer);
        assertTrue(trainer.isActive());
    }

    @Test
    @WithMockUser(username = "Sarah.Connor", roles = {"ADMIN"})
    void testShouldDeactivateTrainee() throws Exception {
        var request = new TrainerRequestDTO();
        request.setFirstName("Jastin");
        request.setLastName("Trudo");
        request.setSpecialization("JavaScript");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseBody, RegistrationResponseDTO.class);
        String username = response.getUsername();
        String password = response.getPassword();

        mockMvc.perform(patch("/api/v1/trainers/activation")
                        .header("X-Username", username)
                        .header("X-Password", password)
                        .param("username", username)
                        .param("isActive", "false"))
                .andExpect(status().isOk());

//        mockMvc.perform(patch("/api/v1/trainers/activation")
//                        .header("X-Username", username)
//                        .header("X-Password", password)
//                        .param("username", username)
//                        .param("isActive", "false"))
//                .andExpect(status().isOk());

        var trainer = trainerService.findByUsername(username);
        assertNotNull(trainer);
        assertFalse(trainer.isActive());
    }

    @Test
    @WithMockUser(username = "Sarah.Connor",  roles = {"TRAINER", "ADMIN"})
    void testShouldUpdateTrainerProfile() throws Exception {
        var request = new TrainerRequestDTO();
        request.setFirstName("Jastin");
        request.setLastName("Trudo");
        request.setSpecialization("JavaScript");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        var response = objectMapper.readValue(responseBody, RegistrationResponseDTO.class);
        String username = response.getUsername();

        String updateRequestJson = """
                {
                    "username": "%s",
                    "firstName": "Kevin",
                    "lastName": "Wayne",
                    "specialization": "Javascript",
                    "isActive": false
                }
                """.formatted(username);

        mockMvc.perform(put("/api/v1/trainers/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("Kevin"))
                .andExpect(jsonPath("$.lastName").value("Wayne"))
                .andExpect(jsonPath("$.specialization").value("Javascript"))
                .andExpect(jsonPath("$.isActive").value(false));

        var trainer = trainerService.findByUsername(username);
        assertNotNull(trainer);
        assertEquals("Kevin", trainer.getFirstName());
        assertEquals("Wayne", trainer.getLastName());
        assertEquals("JAVASCRIPT", trainer.getSpecialization().getTrainingTypeName().name());
        assertFalse(trainer.isActive());
    }
}
