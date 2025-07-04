package com.leultewolde.hidmo.kmingredientsservice.integration;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MinioStorageIntegrationTest {

    @Autowired
    private MinioClient minioClient;

    @Test
    void shouldFailToGenerateUrlWhenServerUnavailable() {
        assertThrows(Exception.class, () -> minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket("test-kitchen-management-app-storage")
                        .object("integration-test.txt")
                        .method(Method.GET)
                        .expiry(60 * 5)
                        .build()
        ));
    }

    @Test
    void shouldThrowForInvalidBucket() {
        assertThrows(Exception.class, () -> minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket("invalid-bucket")
                        .object("file.txt")
                        .method(Method.GET)
                        .expiry(60)
                        .build()
        ));
    }
}
