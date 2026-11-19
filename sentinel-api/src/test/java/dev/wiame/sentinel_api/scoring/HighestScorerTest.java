package dev.wiame.sentinel_api.scoring;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;

class HighestScorerTest {

    private final HighestScorer scorer =
            new HighestScorer(new RuleNameScorer(), new NamespaceScorer());

    private SecurityEvents event(String rule, String namespace) {
        return new SecurityEvents(rule, "HIGH", namespace, "victim", Instant.now());
    }

    @Test
    void ruleWinsWhenItIsMoreSevere() {
        // rule: HIGH, namespace (demo): MEDIUM
        assertThat(scorer.score(event("Terminal shell in container", "demo")))
                .isEqualTo(Severity.HIGH);
    }

    @Test
    void namespaceWinsWhenItIsMoreSevere() {
        // rule: LOW, namespace (kube-system): CRITICAL
        assertThat(scorer.score(event("Something unrelated", "kube-system")))
                .isEqualTo(Severity.CRITICAL);
    }
}