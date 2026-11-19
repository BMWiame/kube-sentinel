package dev.wiame.sentinel_api.scoring;

import java.util.Map;

import org.springframework.stereotype.Service;

import dev.wiame.sentinel_api.config.SentinelConfig;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;
@Service
public class ScoringService {
     private final Map<String, SeverityScorer> scorers;

    public ScoringService(Map<String, SeverityScorer> scorers) {
        this.scorers = scorers;
    }
public Severity score(SecurityEvents event) {
    String mode = SentinelConfig.getInstance().getScoringMode();
    SeverityScorer scorer = scorers.getOrDefault(mode, scorers.get("highest"));
    return scorer.score(event);
}
}
