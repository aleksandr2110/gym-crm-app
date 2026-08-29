package epam;

import epam.config.TestConfig;
import epam.repository.TraineeRepository;
import epam.repository.TrainerRepository;
import epam.repository.TrainingRepository;
import epam.service.TraineeService;
import epam.service.TrainerService;
import epam.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
public class AllBeansTest {

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void testAllDaoBeansExist() {
        assertNotNull(applicationContext.getBean(TraineeRepository.class));
        assertNotNull(applicationContext.getBean(TrainerRepository.class));
        assertNotNull(applicationContext.getBean(TrainingRepository.class));
    }

    @Test
    void testAllServiceBeansExist() {
        assertNotNull(applicationContext.getBean(TraineeService.class));
        assertNotNull(applicationContext.getBean(TrainerService.class));
        assertNotNull(applicationContext.getBean(TrainingService.class));
    }

}
