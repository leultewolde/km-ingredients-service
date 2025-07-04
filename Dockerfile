FROM gradle:jdk24-corretto AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /app/build/libs/km-ingredients-service-*.jar app.jar
COPY newrelic/ /newrelic/
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/newrelic/newrelic.jar", "-jar", "app.jar"]