package dev.wiame.sentinel_api.dto;
import dev.wiame.sentinel_api.model.ScoredEvent;
import dev.wiame.sentinel_api.model.SecurityEvents;

public record EventView(String time, String rule, String namespace, String  pod, String severity) {
    public static EventView from(ScoredEvent scored) {
        SecurityEvents e = scored.event();
        return new EventView(
            e.time().toString(),
            e.rule(),
            e.namespace(),
            e.pod(),
            scored.severity().name()
        );
    }
}
