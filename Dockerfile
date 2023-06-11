FROM openjdk:21-oraclelinux8
WORKDIR usr/app/
COPY /target/*.jar app.jar
RUN useradd -u 1001 tech_user
RUN chown -R tech_user /usr/app
ENTRYPOINT ["java", "-jar", "app.jar"]