package com.leultewolde.hidmo.kmingredientsservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class MinioPropertiesValidationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @Test
    void failsWhenRequiredPropertiesAreMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasFailed();

            Throwable rootCause = getRootCause(context.getStartupFailure());
            assertThat(rootCause)
                    .isInstanceOf(BindValidationException.class)
                    .hasMessageContaining("minio");
        });
    }

    @Test
    void failsWhenPropertiesAreBlank() {
        contextRunner.withPropertyValues(
                "minio.url=",
                "minio.access-name=",
                "minio.access-secret=",
                "minio.bucket-name="
        ).run(context -> {
            assertThat(context).hasFailed();

            Throwable rootCause = getRootCause(context.getStartupFailure());
            assertThat(rootCause)
                    .isInstanceOf(BindValidationException.class)
                    .hasMessageContaining("minio");
        });
    }

    private Throwable getRootCause(Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    @Configuration
    @EnableConfigurationProperties(MinioProperties.class)
    static class TestConfig {}
}
