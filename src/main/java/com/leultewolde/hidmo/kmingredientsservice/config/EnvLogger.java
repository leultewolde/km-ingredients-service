package com.leultewolde.hidmo.kmingredientsservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Slf4j
@Component
public class EnvLogger implements ApplicationRunner {

    private final Environment env;

    public EnvLogger(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== Application Environment Variables ===");

        String[] nonSensitiveKeys = {
                "SPRING_PROFILES_ACTIVE",
                "DB_URL",
                "DB_USER",
                "MINIO_URL",
                "MINIO_BUCKET",
                "splunk.url"
        };

        Arrays.stream(nonSensitiveKeys).forEach(key -> {
            String value = env.getProperty(key);
            if (value != null) {
                log.info("{} = {}", key, value);
            }
        });

        log.info("========================================");
    }
}
