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
    void shouldFailWhenServerUnavailable() {
        assertThrows(Exception.class, () -> minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket("mybucket")
                        .object("test-object.txt")
                        .method(Method.GET)
                        .expiry(60 * 10)
                        .build()
        ));
    }
}
