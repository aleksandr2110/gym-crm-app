package epam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.application.FacadeGymCrmSystem;
import epam.controller.exception.ExceptionHandlerController;
import epam.controller.exception.UnauthorizedException;
import epam.controller.interfaces.TraineeController;
import epam.controller.rest.TraineeControllerImpl;
import epam.domain.dto.request.ChangePasswordRequestDTO;
import epam.domain.dto.request.TraineeRequestDTO;
import epam.domain.dto.response.RegistrationResponseDTO;
import epam.domain.dto.response.TraineeProfileDTO;
import epam.domain.dto.response.TrainerInfoDTO;
import epam.security.util.AuthenticatedUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class TraineeControllerTest {

    @Mock
    private FacadeGymCrmSystem facadeGymCrmSystem;
    @Mock
    private AuthenticatedUserUtil authenticatedUserUtil;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        TraineeController controller = new TraineeControllerImpl(facadeGymCrmSystem, authenticatedUserUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnCreatedTrainee() throws Exception {
        var newTrainee = new TraineeRequestDTO();
        newTrainee.setFirstName("Josh");
        newTrainee.setLastName("Builder");
        newTrainee.setAddress("434 fly road st");
        var registrationResponseDTO = new RegistrationResponseDTO();
        registrationResponseDTO.setUsername("Josh.Builder");
        registrationResponseDTO.setPassword("fdsfdsf798");

        Mockito.when(facadeGymCrmSystem.createTrainee(Mockito.any())).thenReturn(registrationResponseDTO);

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrainee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Josh.Builder"))
                .andExpect(jsonPath("$.password").value("fdsfdsf798"));
    }

    @Test
    void shouldReturnTraineeByUsername() throws Exception {
        var newTrainee = new TraineeRequestDTO();
        newTrainee.setFirstName("Josh");
        newTrainee.setLastName("Builder");
        newTrainee.setAddress("434 fly road st");
        var registrationResponseDTO = new RegistrationResponseDTO();
        registrationResponseDTO.setUsername("Josh.Builder");
        registrationResponseDTO.setPassword("fdsfdsf798");

        Mockito.when(facadeGymCrmSystem.createTrainee(Mockito.any())).thenReturn(registrationResponseDTO);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrainee)))
                .andExpect(status().isCreated())
                .andReturn();

        String registrationResponse = registerResult.getResponse().getContentAsString();
        var registrationResp = objectMapper.readValue(registrationResponse, RegistrationResponseDTO.class);
        String username = registrationResp.getUsername();

        var profileDTO = new TraineeProfileDTO();
        profileDTO.setUsername("Josh.Builder");
        profileDTO.setFirstName("Josh");
        profileDTO.setLastName("Builder");
        profileDTO.setDateOfBirth(LocalDate.of(1990, 07, 26));
        profileDTO.setAddress("11 Green st");
        profileDTO.setIsActive(false);
        profileDTO.setTrainers(List.of());

        Mockito.when(facadeGymCrmSystem.getTraineeByUsername(Mockito.any())).thenReturn(profileDTO);

        mockMvc.perform(get("/api/v1/trainees/" + username)
                        .header("X-Username", "Josh")
                        .header("X-Password", "34322ds"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("Josh"))
                .andExpect(jsonPath("$.lastName").value("Builder"))
                .andExpect(jsonPath("$.dateOfBirth").exists())
                .andExpect(jsonPath("$.address").value("11 Green st"))
                .andExpect(jsonPath("$.isActive").value(false))
                .andExpect(jsonPath("$.trainers").isArray());
    }

    @Test
    void shouldThrowExceptionWhenNotAuthorized() throws Exception {
        String username = "Josef.Marti";
        Mockito.when(facadeGymCrmSystem.getTraineeByUsername(Mockito.any())).thenThrow(new UnauthorizedException("User is not authenticated: " + username));

        mockMvc.perform(get("/api/v1/trainees/"+ username)
                        .header("X-Username", "Josh")
                        .header("X-Password", "34322ds"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldDeleteTraineeByUsername() throws Exception  {
        var newTrainee = new TraineeRequestDTO();
        newTrainee.setFirstName("Josh");
        newTrainee.setLastName("Builder");
        newTrainee.setAddress("434 fly road st");

        var registrationResponseDTO = new RegistrationResponseDTO();
        registrationResponseDTO.setUsername("Josh.Builder");
        registrationResponseDTO.setPassword("fdsfdsf798");

        Mockito.when(facadeGymCrmSystem.createTrainee(Mockito.any())).thenReturn(registrationResponseDTO);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .header("X-Username", "Josh")
                        .header("X-Password", "34322ds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrainee)))
                .andExpect(status().isCreated())
                .andReturn();

        String registrationResponse = registerResult.getResponse().getContentAsString();
        var registrationResp = objectMapper.readValue(registrationResponse, RegistrationResponseDTO.class);
        String username = registrationResp.getUsername();

        Mockito.doNothing().when(facadeGymCrmSystem).deleteTrainee(Mockito.any());

        mockMvc.perform(delete("/api/v1/trainees/" + username)
                        .header("X-Username", "Josh")
                        .header("X-Password", "34322ds"))
                .andExpect(status().isNoContent());

        verify(facadeGymCrmSystem, times(1)).deleteTrainee(Mockito.any());
    }

    @Test
    void shouldGetAvailableTrainers() throws Exception {
        var newTrainee = new TraineeRequestDTO();
        newTrainee.setFirstName("Josh");
        newTrainee.setLastName("Builder");
        newTrainee.setAddress("434 fly road st");
        var registrationResponseDTO = new RegistrationResponseDTO();
        registrationResponseDTO.setUsername("Josh.Builder");
        registrationResponseDTO.setPassword("fdsfdsf798");

        Mockito.when(facadeGymCrmSystem.createTrainee(Mockito.any())).thenReturn(registrationResponseDTO);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrainee)))
                .andExpect(status().isCreated())
                .andReturn();

        String registrationResponse = registerResult.getResponse().getContentAsString();
        var registrationResp = objectMapper.readValue(registrationResponse, RegistrationResponseDTO.class);
        String username = registrationResp.getUsername();

        Mockito.when(facadeGymCrmSystem.getAvailableTrainers(Mockito.any())).thenReturn(createTrainers());

        mockMvc.perform(get("/api/v1/trainees/" + username + "/available-trainers")
                        .header("X-Username", "Josh")
                        .header("X-Password", "34322ds"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void shouldUpdateTraineeProfile() throws Exception {
        var newTrainee = new TraineeRequestDTO();
        newTrainee.setFirstName("Josh");
        newTrainee.setLastName("Builder");
        newTrainee.setAddress("434 fly road st");
        var registrationResponseDTO = new RegistrationResponseDTO();
        registrationResponseDTO.setUsername("Josh.Builder");
        registrationResponseDTO.setPassword("fdsfdsf798");

        Mockito.when(facadeGymCrmSystem.createTrainee(Mockito.any())).thenReturn(registrationResponseDTO);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrainee)))
                .andExpect(status().isCreated())
                .andReturn();

        String registrationResponse = registerResult.getResponse().getContentAsString();
        var registrationResp = objectMapper.readValue(registrationResponse, RegistrationResponseDTO.class);
        String username = registrationResp.getUsername();

        String updateRequestJson = """
                {
                    "username": "%s",
                    "firstName": "James",
                    "lastName": "Halpert",
                    "dateOfBirth": "1987-11-01",
                    "address": "455 Light Ave",
                    "isActive": false
                }
                """.formatted(username);

        var profile = new TraineeProfileDTO();
        profile.setUsername("Josh.Builder");
        profile.setFirstName("James");
        profile.setLastName("Halpert");
        profile.setAddress("455 Light Ave");
        profile.setIsActive(false);

        Mockito.when(facadeGymCrmSystem.updateTraineeProfile(Mockito.any()))
                .thenReturn(profile);

        mockMvc.perform(put("/api/v1/trainees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("James"))
                .andExpect(jsonPath("$.lastName").value("Halpert"))
                .andExpect(jsonPath("$.address").value("455 Light Ave"))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    public void shouldChangePasswordTrainee() throws Exception {
        var newTrainee = new TraineeRequestDTO();
        newTrainee.setFirstName("Josh");
        newTrainee.setLastName("Builder");
        newTrainee.setAddress("434 fly road st");
        var registrationResponseDTO = new RegistrationResponseDTO();
        registrationResponseDTO.setUsername("Josh.Builder");
        registrationResponseDTO.setPassword("fdsfdsf798");

        Mockito.when(facadeGymCrmSystem.createTrainee(Mockito.any())).thenReturn(registrationResponseDTO);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrainee)))
                .andExpect(status().isCreated())
                .andReturn();

        String registrationResponse = registerResult.getResponse().getContentAsString();
        var registrationResp = objectMapper.readValue(registrationResponse, RegistrationResponseDTO.class);
        String username = registrationResp.getUsername();
        String oldPassword = registrationResp.getPassword();

        var changePasswordRequest = new ChangePasswordRequestDTO();
        changePasswordRequest.setUsername(username);
        changePasswordRequest.setOldPassword(oldPassword);
        changePasswordRequest.setNewPassword("newPass5435");

        Mockito.doNothing().when(facadeGymCrmSystem).changeTraineePassword(Mockito.any());

        mockMvc.perform(put("/api/v1/trainees/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .header("X-Username", "Josh")
                        .header("X-Password", "34322ds"))
                .andExpect(status().isOk());
        verify(facadeGymCrmSystem, times(1)).changeTraineePassword(Mockito.any());
    }

    @Test
    public void shouldActivateTrainee() throws Exception {
        var newTrainee = new TraineeRequestDTO();
        newTrainee.setFirstName("Josh");
        newTrainee.setLastName("Builder");
        newTrainee.setAddress("434 fly road st");
        var registrationResponseDTO = new RegistrationResponseDTO();
        registrationResponseDTO.setUsername("Josh.Builder");
        registrationResponseDTO.setPassword("fdsfdsf798");

        Mockito.when(facadeGymCrmSystem.createTrainee(Mockito.any())).thenReturn(registrationResponseDTO);

        MvcResult registerResult = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrainee)))
                .andExpect(status().isCreated())
                .andReturn();

        String registrationResponse = registerResult.getResponse().getContentAsString();
        var registrationResp = objectMapper.readValue(registrationResponse, RegistrationResponseDTO.class);
        String username = registrationResp.getUsername();

        doNothing().when(facadeGymCrmSystem).activateDeactivateTrainee(username, true);

        mockMvc.perform(patch("/api/v1/trainees/activation")
                        .param("username", username)
                        .param("isActive", "true"))
                .andExpect(status().isOk());

        verify(facadeGymCrmSystem, times(1)).activateDeactivateTrainee(username, true);
    }

    private static List<TrainerInfoDTO> createTrainers() {
        List<TrainerInfoDTO> trainers = List.of(new TrainerInfoDTO(1L, "Jonson.Gosh", "Jonson",
                "Gosh", "Java"), new TrainerInfoDTO(2L, "Jack.Gosh", "Jack",
                "Gosh", "JavaScript"));
        return trainers;
    }

}
