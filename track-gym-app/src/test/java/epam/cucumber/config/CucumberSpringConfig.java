package epam.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


@CucumberContextConfiguration
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
public class CucumberSpringConfig {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        mongo.start();
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.data.mongodb.host", mongo::getHost);
        registry.add("spring.data.mongodb.port", mongo::getFirstMappedPort);
    }

    @EnableTransactionManagement
    @ComponentScan(basePackages = {
            "epam.domain",
            "epam.repository",
            "epam.service",
            "epam.util"
    })
    @TestConfiguration
    public static class Configuration  {
    }



    static final RabbitMQContainer RABBIT_MQ_CONTAINER = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:4-management")
    );

    static {
        RABBIT_MQ_CONTAINER.start();
    }

    @DynamicPropertySource
    static void rabbitMqProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", RABBIT_MQ_CONTAINER::getHost);
        registry.add("spring.rabbitmq.port", RABBIT_MQ_CONTAINER::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBIT_MQ_CONTAINER::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBIT_MQ_CONTAINER::getAdminPassword);
    }

}
