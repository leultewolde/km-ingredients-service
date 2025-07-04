package com.leultewolde.hidmo.kmingredientsservice.integration;

import io.minio.MinioClient;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class MinioStorageIntegrationTest {

    @Autowired
    private MinioClient minioClient;

    @Test
    void shouldGenerateUrlWhenServerUnavailable() throws Exception {
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket("test-kitchen-management-app-storage")
                        .object("integration-test.txt")
                        .method(Method.GET)
                        .region("us-east-1")
                        .expiry(60 * 5)
                        .build()
        );
        assertNotNull(url);
    }

    @Test
    void shouldGenerateUrlForInvalidBucket() throws Exception {
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket("invalid-bucket")
                        .object("file.txt")
                        .method(Method.GET)
                        .region("us-east-1")
                        .expiry(60)
                        .build()
        );
        assertNotNull(url);
    }
}
