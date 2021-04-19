FROM gradle:jdk13 AS cache
WORKDIR /home/gradle/app
ENV GRADLE_USER_HOME /home/gradle/cache
COPY build.gradle gradle.properties settings.gradle ./
RUN gradle --no-daemon build --stacktrace

FROM gradle:jdk13 AS builder
WORKDIR /home/gradle/app
COPY --from=cache /home/gradle/cache /home/gradle/.gradle
COPY . ./
RUN gradle --no-daemon build --stacktrace

FROM openjdk:jre-alpine
WORKDIR /home/gradle/app
COPY --from=builder /home/gradle/app/build/libs/app.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]