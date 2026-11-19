package dev.wiame.sentinel_api.scoring;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import dev.wiame.sentinel_api.config.SentinelConfig;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;

class ScoringServiceTest {

    private final RuleNameScorer ruleScorer = new RuleNameScorer();
    private final NamespaceScorer namespaceScorer = new NamespaceScorer();
    private final ScoringService service = new ScoringService(Map.of(
            "rule", ruleScorer,
            "namespace", namespaceScorer,
            "highest", new HighestScorer(ruleScorer, namespaceScorer)));

    @AfterEach
    void resetMode() {
        SentinelConfig.getInstance().setScoringMode("highest");
    }

    private SecurityEvents event(String rule, String namespace) {
        return new SecurityEvents(rule, "HIGH", namespace, "victim", Instant.now());
    }

    @Test
    void modeSelectsTheScorer() {
        SecurityEvents shellInDemo = event("Terminal shell in container", "demo");

        SentinelConfig.getInstance().setScoringMode("namespace");
        Severity byNamespace = service.score(shellInDemo);

        SentinelConfig.getInstance().setScoringMode("rule");
        Severity byRule = service.score(shellInDemo);

        assertThat(byNamespace).isEqualTo(Severity.MEDIUM);
        assertThat(byRule).isEqualTo(Severity.HIGH);
        assertThat(byNamespace).isNotEqualTo(byRule);
    }

    @Test
    void unknownModeFallsBackToHighest() {
        SentinelConfig.getInstance().setScoringMode("apple");

        // rule: HIGH, namespace: MEDIUM -> highest is HIGH (not the namespace result)
        assertThat(service.score(event("Terminal shell in container", "demo")))
                .isEqualTo(Severity.HIGH);

        // rule: LOW, namespace: CRITICAL -> highest is CRITICAL (not the rule result)
        assertThat(service.score(event("Something unrelated", "kube-system")))
                .isEqualTo(Severity.CRITICAL);
    }
}