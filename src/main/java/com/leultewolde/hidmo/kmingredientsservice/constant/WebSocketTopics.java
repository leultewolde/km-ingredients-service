package com.leultewolde.hidmo.kmingredientsservice.constant;

/**
 * Central place for WebSocket topic names to avoid repeated literals.
 */
public final class WebSocketTopics {
    private WebSocketTopics() {}

    public static final String INGREDIENTS = "/topic/ingredients";
    public static final String PREPARED_FOODS = "/topic/prepared-foods";
}
