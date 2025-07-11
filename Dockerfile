FROM gradle:7.6-jdk17-alpine AS build
WORKDIR /app
COPY . /app
RUN gradle clean test
RUN gradle clean bootJar

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/BotPCD-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 5432
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]