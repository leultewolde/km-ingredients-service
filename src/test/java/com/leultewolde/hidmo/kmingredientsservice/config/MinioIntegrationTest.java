package com.leultewolde.hidmo.kmingredientsservice.config;

import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "minio.url=https://minio.example.com",
        "minio.access-name=minioadmin",
        "minio.access-secret=minioadmin123",
        "minio.bucket-name=mybucket"
})
@ActiveProfiles("test")
class MinioIntegrationTest {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    @Test
    void contextLoadsAndBeansAreAvailable() {
        assertNotNull(minioClient);
        assertEquals("https://minio.example.com", minioProperties.getUrl());
    }
}
