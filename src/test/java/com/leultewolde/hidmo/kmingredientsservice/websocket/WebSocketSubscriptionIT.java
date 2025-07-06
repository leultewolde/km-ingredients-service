package com.leultewolde.hidmo.kmingredientsservice.websocket;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class WebSocketSubscriptionIT {

    @LocalServerPort
    int port;

    @Autowired
    private IngredientService ingredientService;

    @Test
    void subscriberReceivesUpdatesWhenIngredientCreated() throws Exception {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        BlockingQueue<List> queue = new LinkedBlockingQueue<>();

        StompSession session = stompClient
                .connect(String.format("ws://localhost:%d/ws", port), new WebSocketHttpHeaders(),
                        new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);

        session.subscribe("/topic/ingredients", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return List.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                queue.offer((List) payload);
            }
        });

        IngredientRequestDTO dto = new IngredientRequestDTO(
                "WS Ingredient", BigDecimal.ONE, "pc",
                BigDecimal.ONE, LocalDate.now(),
                LocalDate.now().plusDays(1), "Pantry",
                IngredientStatus.AVAILABLE, "WS-1", null
        );

        ingredientService.create(dto);

        List result = queue.poll(5, TimeUnit.SECONDS);
        assertNotNull(result, "No websocket message received");
        assertFalse(result.isEmpty());
    }
}
