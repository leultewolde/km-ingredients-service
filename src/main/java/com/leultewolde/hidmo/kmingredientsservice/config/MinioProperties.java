package com.leultewolde.hidmo.kmingredientsservice.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    @NotBlank(message = "MinIO URL must not be blank")
    private String url;

    @NotBlank(message = "MinIO access name must not be blank")
    private String accessName;

    @NotBlank(message = "MinIO access secret must not be blank")
    private String accessSecret;

    @NotBlank(message = "MinIO bucket name must not be blank")
    private String bucketName;
}
