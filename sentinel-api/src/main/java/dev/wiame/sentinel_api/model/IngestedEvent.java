package dev.wiame.sentinel_api.model;

public record IngestedEvent(SecurityEvents event, String rawJson) {
    
}
