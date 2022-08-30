FROM adoptopenjdk/openjdk11:ubi
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY application-jwt.properties application-jwt.properties
COPY application-real-db.properties application-real-db.properties
CMD java \
-jar \
-Dspring.config.location=classpath:/application.properties,application-jwt.properties,application-real-db.properties \
/app.jar