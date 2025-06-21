package com.leultewolde.hidmo.kmingredientsservice.config;

import io.minio.MinioClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MinioConfigTest {

    @Test
    void shouldCreateMinioClientFromProperties() {
        MinioProperties props = new MinioProperties();
        props.setUrl("https://minio.example.com");
        props.setAccessName("minioadmin");
        props.setAccessSecret("minioadmin123");
        props.setBucketName("mybucket");

        MinioClient client = new MinioConfig().minioClient(props);

        assertNotNull(client);
    }
}
