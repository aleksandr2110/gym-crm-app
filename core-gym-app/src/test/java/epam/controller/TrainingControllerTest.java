package epam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.application.FacadeGymCrmSystem;
import epam.controller.interfaces.TrainingController;
import epam.controller.rest.TrainingControllerImpl;
import epam.domain.dto.request.TraineeTrainingsRequestDTO;
import epam.domain.dto.request.TrainerTrainingsRequestDTO;
import epam.domain.dto.request.TrainingRequestDTO;
import epam.domain.dto.response.TrainingTraineeDTO;
import epam.domain.dto.response.TrainingTrainerDTO;
import epam.domain.dto.response.TrainingTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TrainingControllerTest {

    @Mock
    private FacadeGymCrmSystem facadeGymCrmSystem;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        TrainingController controller = new TrainingControllerImpl(facadeGymCrmSystem);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void getTrainingTypes() throws Exception {
        var trainingType1 = new TrainingTypeDTO();
        trainingType1.setId(1L);
        trainingType1.setTrainingTypeName("Java");
        var trainingType2 = new TrainingTypeDTO();
        trainingType2.setId(2L);
        trainingType2.setTrainingTypeName("Python");
        var trainingType3 = new TrainingTypeDTO();
        trainingType3.setId(3L);
        trainingType3.setTrainingTypeName("JavaScript");
        List<TrainingTypeDTO> types = new ArrayList<>();
        types.add(trainingType1);
        types.add(trainingType2);
        types.add(trainingType3);

        Mockito.when(facadeGymCrmSystem.getTrainingTypes()).thenReturn(types);

        mockMvc.perform(get("/api/v1/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].trainingTypeName").exists());
    }

    @Test
    public void shouldReturnCreatedTraining() throws Exception {
        TrainingRequestDTO trainingRequest = new TrainingRequestDTO();
        trainingRequest.setTraineeUsername("Kevin.Josh");
        trainingRequest.setTrainerUsername("Jeff.Gosling");
        trainingRequest.setTrainingName("Java learning");
        trainingRequest.setTrainingType("Java");
        trainingRequest.setTrainingDate("2026-07-29T18:30:00");
        trainingRequest.setTrainingDuration(60);

        Mockito.doNothing().when(facadeGymCrmSystem).createTraining(Mockito.any());

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingRequest)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        verify(facadeGymCrmSystem, times(1)).createTraining(Mockito.any());
    }

    @Test
    public void shouldReturnTraineeTraining() throws Exception {
        var filterRequest = new TraineeTrainingsRequestDTO();
        filterRequest.setUsername("Kevin.Josh");
        filterRequest.setTrainingType("Java");
        filterRequest.setPeriodFrom("2026-07-10 20:38:00");
        filterRequest.setPeriodTo("2026-07-30 20:38:00");
        filterRequest.setTrainerName("Java learning");

        Mockito.when(facadeGymCrmSystem.getTraineeTrainings(Mockito.any()))
                .thenReturn(getTrainings());

        mockMvc.perform(get("/api/v1/trainings/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest))
                        .header("X-Username", "Josh")
                        .header("X-Password", "34322ds"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].trainerName").exists())
                .andExpect(jsonPath("$[0].trainingType").exists());
    }

    @Test
    public void shouldReturnTrainerTraining() throws Exception {
        var filterRequest = new TrainerTrainingsRequestDTO();
        filterRequest.setUsername("Kevin.Josh");
        filterRequest.setPeriodFrom("2026-07-10 20:38:00");
        filterRequest.setPeriodTo("2026-07-30 20:38:00");
        filterRequest.setTraineeName("Stive.Jobs");

        Mockito.when(facadeGymCrmSystem.getTrainerTrainings(Mockito.any()))
                .thenReturn(getTrainerTrainings());

        mockMvc.perform(get("/api/v1/trainings/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].traineeName").value("Jeff.Gosling"))
                .andExpect(jsonPath("$[0].trainingType").value("Java"));
    }

    private List<TrainingTraineeDTO> getTrainings() {
        var traning = new TrainingTraineeDTO();
        traning.setTrainerName("Jeff.Gosling");
        traning.setTrainingType("Java");
        traning.setTrainingName("Java learning");
        traning.setTrainingDate(LocalDateTime.of(2026, 7, 12, 18, 30));
        traning.setTrainingDuration(50);
        var traning2 = new TrainingTraineeDTO();
        traning2.setTrainerName("Stive.Vitkov");
        traning2.setTrainingType("Python");
        traning2.setTrainingName("Python learning");
        traning2.setTrainingDate(LocalDateTime.of(2026, 7, 17, 18, 30));
        traning2.setTrainingDuration(50);
        List<TrainingTraineeDTO> trainings = new ArrayList<>(Arrays.asList(traning, traning2));
        return trainings;
    }
    public List<TrainingTrainerDTO> getTrainerTrainings() {
        var traning = new TrainingTrainerDTO();
        traning.setTraineeName("Jeff.Gosling");
        traning.setTrainingType("Java");
        traning.setTrainingName("Java learning");
        traning.setTrainingDate(LocalDateTime.of(2026, 7, 12, 18, 30));
        traning.setTrainingDuration(50);
        var traning2 = new TrainingTrainerDTO();
        traning2.setTraineeName("Stive.Vitkov");
        traning2.setTrainingType("Python");
        traning2.setTrainingName("Python learning");
        traning2.setTrainingDate(LocalDateTime.of(2026, 7, 17, 18, 30));
        traning2.setTrainingDuration(50);
        List<TrainingTrainerDTO> trainings = new ArrayList<>(Arrays.asList(traning, traning2));
        return trainings;
    }
}
