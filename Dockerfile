FROM maven:3.9.9-eclipse-temurin-21-alpine

VOLUME /tmp

ENV JAVA_OPTS=""

ARG JAR_FILE=target/test-project-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]