FROM eclipse-temurin:17-jdk

RUN mkdir /opt/app
WORKDIR /opt/app
EXPOSE 8080

ARG JAR_NAME=build/libs/*-SNAPSHOT.jar

COPY ${JAR_NAME} app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.config.location=classpath:/application.yml", "app.jar"]
