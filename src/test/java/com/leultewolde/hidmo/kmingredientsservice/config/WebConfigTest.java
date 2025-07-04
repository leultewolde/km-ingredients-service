package com.leultewolde.hidmo.kmingredientsservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.mockito.Mockito.*;

class WebConfigTest {

    @Test
    void addCorsMappings_configuresRegistry() {
        WebConfig config = new WebConfig();
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(registration);
        when(registration.allowedOrigins("*")).thenReturn(registration);
        when(registration.allowedMethods("*")).thenReturn(registration);
        when(registration.allowedHeaders("*")).thenReturn(registration);

        config.addCorsMappings(registry);

        verify(registry).addMapping("/**");
        verify(registration).allowedOrigins("*");
        verify(registration).allowedMethods("*");
        verify(registration).allowedHeaders("*");
    }
}
