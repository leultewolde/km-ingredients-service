package com.leultewolde.hidmo.kmingredientsservice.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.splunk.logging.HttpEventCollectorLogbackAppender;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class SplunkLoggingConfigTest {

    @Test
    void configureSplunkAppender_addsAppenderToRootLogger() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        // remove existing appender if present
        if (rootLogger.getAppender("SPLUNK") != null) {
            rootLogger.detachAppender("SPLUNK");
        }

        SplunkLoggingConfig config = new SplunkLoggingConfig();
        ReflectionTestUtils.setField(config, "splunkUrl", "http://example.com");
        ReflectionTestUtils.setField(config, "splunkToken", "token");
        ReflectionTestUtils.setField(config, "logPattern", "%msg");

        config.configureSplunkAppender();

        var appender = rootLogger.getAppender("SPLUNK");
        assertNotNull(appender, "Appender should be attached");
        assertTrue(appender instanceof HttpEventCollectorLogbackAppender);
        HttpEventCollectorLogbackAppender<ILoggingEvent> splunkAppender =
                (HttpEventCollectorLogbackAppender<ILoggingEvent>) appender;
        assertEquals("http://example.com", splunkAppender.getUrl());
        assertEquals("token", splunkAppender.getToken());
        assertNotNull(splunkAppender.getLayout());
    }
}
