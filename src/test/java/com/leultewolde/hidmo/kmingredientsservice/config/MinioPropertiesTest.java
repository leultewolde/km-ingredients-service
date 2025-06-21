package com.leultewolde.hidmo.kmingredientsservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MinioPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "minio.url=https://minio.example.com",
                    "minio.access-name=minioadmin",
                    "minio.access-secret=minioadmin123",
                    "minio.bucket-name=mybucket"
            );

    @Test
    void bindsConfigurationPropertiesCorrectly() {
        contextRunner.run(context -> {
            MinioProperties props = context.getBean(MinioProperties.class);
            assertEquals("https://minio.example.com", props.getUrl());
            assertEquals("minioadmin", props.getAccessName());
            assertEquals("minioadmin123", props.getAccessSecret());
            assertEquals("mybucket", props.getBucketName());
        });
    }

    @EnableConfigurationProperties(MinioProperties.class)
    static class TestConfig {}
}