package com.leultewolde.hidmo.kmingredientsservice.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.splunk.logging.HttpEventCollectorLogbackAppender;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"prod", "dev"})
@Configuration
public class SplunkLoggingConfig {

    @Value("${splunk.url}")
    private String splunkUrl;

    @Value("${splunk.token}")
    private String splunkToken;

    @Value("${logging.pattern.console:%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n}")
    private String logPattern;

    @PostConstruct
    public void configureSplunkAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Create and configure PatternLayout
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setPattern(logPattern);
        patternLayout.setContext(context);
        patternLayout.start();

        // Create and configure Splunk appender
        HttpEventCollectorLogbackAppender<ILoggingEvent> splunkAppender = new HttpEventCollectorLogbackAppender<>();
        splunkAppender.setName("SPLUNK");
        splunkAppender.setUrl(splunkUrl);
        splunkAppender.setToken(splunkToken);
        splunkAppender.setLayout(patternLayout);
        splunkAppender.setContext(context);
        splunkAppender.start();

        // Attach Splunk appender to root logger
        Logger rootLogger = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(splunkAppender);

        rootLogger.info("✅ Splunk appender added to root logger with URL: {}", splunkUrl);
    }
}
