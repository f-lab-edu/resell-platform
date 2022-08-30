FROM adoptopenjdk/openjdk11:ubi
ARG JAR_FILE=build/libs/*.jar
ARG APP_ROOT=/home/ec2-user/app
COPY ${JAR_FILE} app.jar
COPY classpath:/application.properties application.properties
COPY ${APP_ROOT}/application-jwt.properties application-jwt.properties
COPY ${APP_ROOT}/application-real-db.properties application-real-db.properties
CMD java \
-jar \
-Dspring.config.location=application.properties,application-jwt.properties,application-real-db.properties \
/app.jar