package com.leultewolde.hidmo.kmingredientsservice.component;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MinioStorageComponentTest {

    @Autowired
    private MinioClient minioClient;

    @Test
    void shouldGeneratePresignedUrl() {
        assertDoesNotThrow(() -> {
            String objectName = "test-object.txt";
            URL url = URI.create(minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket("mybucket")
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(60 * 10)
                            .build()
            )).toURL();
            assertNotNull(url.toString());
            assertTrue(url.toString().contains(objectName));
        });
    }
}
