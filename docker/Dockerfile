FROM java:8-jdk-alpine
WORKDIR usr/app/
COPY /build/libs/*.jar usr/app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
