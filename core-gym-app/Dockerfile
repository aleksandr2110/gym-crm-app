
FROM eclipse-temurin:17.0.5_8-jre-focal

WORKDIR /application/

ADD ./target/*.jar app.jar

ENV INTEGRATIONS_ENABLED=false

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar",  "--spring.profiles.active=dev"]