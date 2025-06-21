package com.leultewolde.hidmo.kmingredientsservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class KmIngredientsServiceApplicationTests {

    @Autowired private ApplicationContext context;

    @Test
    void contextLoads() {
        // Simple test to see if application context loads
        assertThat(context).isNotNull();
    }
}
