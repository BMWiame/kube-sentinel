package dev.wiame.sentinel_api.scoring;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;

public interface SeverityScorer {
    Severity score(SecurityEvents event);
}