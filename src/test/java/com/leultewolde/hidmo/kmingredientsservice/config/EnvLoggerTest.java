package com.leultewolde.hidmo.kmingredientsservice.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.core.env.Environment;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnvLoggerTest {

    private Environment env;

    @BeforeEach
    void setUp() {
        env = mock(Environment.class);
    }

    @Test
    void run_logsExpectedVariables() throws Exception {
        when(env.getProperty("SPRING_PROFILES_ACTIVE")).thenReturn("test");
        when(env.getProperty("DB_URL")).thenReturn("jdbc:h2:mem:testdb");
        when(env.getProperty("DB_USER")).thenReturn("sa");
        when(env.getProperty("MINIO_URL")).thenReturn("http://localhost");
        when(env.getProperty("MINIO_BUCKET")).thenReturn("bucket");

        Logger logger = (Logger) LoggerFactory.getLogger(EnvLogger.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        new EnvLogger(env).run(new DefaultApplicationArguments(new String[0]));

        logger.detachAppender(appender);
        List<ILoggingEvent> logs = appender.list;
        assertTrue(logs.stream().anyMatch(e -> e.getFormattedMessage().contains("SPRING_PROFILES_ACTIVE = test")));
        assertTrue(logs.stream().anyMatch(e -> e.getFormattedMessage().contains("DB_URL = jdbc:h2:mem:testdb")));
        assertTrue(logs.stream().anyMatch(e -> e.getFormattedMessage().contains("MINIO_BUCKET = bucket")));
    }
}
