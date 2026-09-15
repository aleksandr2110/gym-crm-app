package epam.cucumber.component.config;

import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@CucumberContextConfiguration
@SpringBootTest(classes = {
        CucumberSpringConfig.Configuration.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfig {

    @MockitoBean
    private RabbitTemplate rabbitTemplate;


    @EnableTransactionManagement
    @ComponentScan(basePackages = {
            "epam.advice",
            "epam.domain",
            "epam.repository",
            "epam.service",
            "epam.util"
    })
    @TestConfiguration
    public static class Configuration {
        @Value("${spring.datasource.url}")
        private String datasourceUrl;

        @Value("${spring.datasource.username}")
        private String datasourceUsername;

        @Value("${spring.datasource.password}")
        private String datasourcePassword;

        @Value("${spring.datasource.driver-class-name}")
        private String driverClassName;

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }

        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(datasourceUrl);
            dataSource.setUsername(datasourceUsername);
            dataSource.setPassword(datasourcePassword);
            dataSource.setDriverClassName(driverClassName);
            return dataSource;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource());
            em.setPackagesToScan("epam.domain.entity");
            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            return em;
        }

        @Bean
        public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
            JpaTransactionManager tm = new JpaTransactionManager();
            tm.setEntityManagerFactory(entityManagerFactory);
            return tm;
        }

        @Bean
        public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
            return SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
        }
    }
}
