package epam.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym CRM System - RESTful API")
                        .description("""
                                A comprehensive Gym Customer Relationship Management (CRM) system providing RESTful APIs \
                                for managing trainees, trainers, training sessions, and training types.

                                ## Key Features

                                * **User Management**: Register and manage trainee and trainer profiles with auto-generated credentials
                                * **Profile Operations**: Update user profiles, activate/deactivate accounts, and change passwords
                                * **Training Management**: Create training sessions, view training history with flexible filtering
                                * **Trainer Assignment**: Manage many-to-many relationships between trainees and trainers
                                * **Training Types**: Support for multiple training specializations (Fitness, Yoga, Zumba, etc.)

                                ## Technical Highlights

                                * RESTful architecture with proper HTTP methods and status codes
                                * Comprehensive request validation using Jakarta Bean Validation
                                * Global exception handling with meaningful error responses
                                * Transaction-level logging with correlation IDs for request tracing
                                * Swagger/OpenAPI documentation for all endpoints

                                ## Notes

                                * All endpoints (except registration) require authentication (will be implemented with Spring Security)
                                * Username generation follows the pattern: `FirstName.LastName` with incremental suffixes for duplicates
                                * Passwords are randomly generated 10-character alphanumeric strings
                                * Cascade delete: Deleting a trainee also removes all associated training sessions
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Aleksandr Oleynik")
                                .url("https://www.linkedin.com/in/aleksandr-olejnik-764189129/")
                                .email("oleksandr_oliinyk5@epam.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("gym-crm-system")
                .packagesToScan("epam.domain.dto","epam.domain.entity")
                .build();
    }
}
