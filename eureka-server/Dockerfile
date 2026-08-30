
FROM eclipse-temurin:17.0.5_8-jre-focal

LABEL Description="Eureka Server" Version="0.0.1"

ARG VERSION=0.0.1

WORKDIR /application/

ADD ./target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]