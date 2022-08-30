FROM adoptopenjdk/openjdk11:ubi
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", \
"-Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-jwt.properties,/home/ec2-user/app/application-real-db.properties", \
"/app.jar", \
"2>&1 &"]
