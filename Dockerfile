FROM gradle:7.6-jdk17-alpine AS build
WORKDIR /app
COPY . /app
RUN gradle clean bootJar

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/BotPCD-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]