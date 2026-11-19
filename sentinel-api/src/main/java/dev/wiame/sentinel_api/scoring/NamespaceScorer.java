package dev.wiame.sentinel_api.scoring;



import org.springframework.stereotype.Component;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;


@Component("namespace")
public class NamespaceScorer implements SeverityScorer {

     @Override
    public Severity score(SecurityEvents event) {
        return switch (event.namespace()) {
            case "kube-system", "falco", "sentinel" -> Severity.CRITICAL;
            case "demo" -> Severity.MEDIUM;
            default -> Severity.LOW;
        };
    }
}