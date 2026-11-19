package dev.wiame.sentinel_api.model;

public record ScoredEvent(SecurityEvents event,Severity severity ,String rawJson) {
    
}
