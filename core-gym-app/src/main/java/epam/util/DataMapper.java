package epam.util;


import epam.domain.dto.request.TraineeRequestDTO;
import epam.domain.dto.request.TrainerRequestDTO;
import epam.domain.dto.request.TrainingRequestDTO;
import epam.domain.dto.request.UpdateTraineeRequestDTO;
import epam.domain.dto.response.TraineeProfileDTO;
import epam.domain.dto.response.TrainerInfoDTO;
import epam.domain.dto.response.TrainerProfileDTO;
import epam.domain.entity.Trainee;
import epam.domain.entity.Trainer;
import epam.domain.entity.Training;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataMapper {

    @Autowired
    private ModelMapper mapper;

    public Trainee toTrainee(TraineeRequestDTO traineeRequestDTO) {
        return mapper.map(traineeRequestDTO, Trainee.class);
    }

    public Trainee toUpdateTrainee(UpdateTraineeRequestDTO traineeRequestDTO) {
        return mapper.map(traineeRequestDTO, Trainee.class);
    }

    public Trainer toTrainer(TrainerRequestDTO trainerRequestDTO) {
        return mapper.map(trainerRequestDTO, Trainer.class);
    }
    public TraineeProfileDTO toProfileTraineeDTO(Trainee entity) {
        return mapper.map(entity, TraineeProfileDTO.class);
    }

    public TrainerProfileDTO toProfileTrainerDTO(Trainer entity) {
        return mapper.map(entity, TrainerProfileDTO.class);
    }

    public TrainerInfoDTO toTrainerDTO(Trainer entity) {
        return mapper.map(entity, TrainerInfoDTO.class);
    }

    public Training toTraining(TrainingRequestDTO trainingRequestDTO) {
        return mapper.map(trainingRequestDTO, Training.class);
    }
}
