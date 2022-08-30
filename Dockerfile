FROM adoptopenjdk/openjdk11:ubi
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/application.properties application.properties
CMD java \
-jar \
-Dspring.config.location=classpath:/application.properties,application-jwt.properties,application-real-db.properties \
/app.jar