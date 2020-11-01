FROM java:8-jdk-alpine
WORKDIR usr/app/
COPY /build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-jar", "app.jar"]