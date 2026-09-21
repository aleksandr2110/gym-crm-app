package epam.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@CucumberContextConfiguration
//@SpringBootTest(properties = {"spring.mongodb.embedded.version=5.0.6"})
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
//@AutoConfigureWebTestClient
@Testcontainers
//@ActiveProfiles("test")
public class CucumberSpringConfig {


    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @EnableTransactionManagement
    @ComponentScan(basePackages = {
            "epam.domain",
            "epam.repository",
            "epam.service",
            "epam.util"
    })
    @TestConfiguration
    @EnableMongoRepositories(basePackages = "epam.repository")
    public static class Configuration {
        //
    }
}
