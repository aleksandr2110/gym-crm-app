package epam.service.impl;

import epam.domain.entity.TrainingType;
import epam.repository.TrainingTypeRepository;
import epam.service.TrainingTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public void saveTrainingType(List<TrainingType> trainingList) {
        trainingTypeRepository.saveTrainingType(trainingList);
    }

    @Override
    public TrainingType findByName(String name) {
        return trainingTypeRepository.findByName(name);
    }

    @Override
    public TrainingType findById(Long id) {
        return trainingTypeRepository.findById(id);
    }

    @Override
    public List<TrainingType> findAll() {
        return trainingTypeRepository.findAll();
    }

}
