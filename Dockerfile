FROM gradle:jdk24-corretto AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:24-jre
WORKDIR /app
RUN apt-get update && apt-get install -y curl jq \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/build/libs/km-ingredients-service-*.jar app.jar
COPY newrelic/ /newrelic/
COPY docker-entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/entrypoint.sh"]

