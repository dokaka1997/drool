FROM openjdk:8-jdk-alpine

ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY ./target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE $PORT
