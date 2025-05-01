FROM gradle:7.6-jdk17-alpine AS build
WORKDIR /app
COPY . /app
RUN gradle clean build -x test

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]