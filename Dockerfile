FROM adoptopenjdk/openjdk11:alpine-slim
ADD target/spring-webflux-movements-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]