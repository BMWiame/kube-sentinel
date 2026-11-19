package dev.wiame.sentinel_api.scoring;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;

class NamespaceScorerTest {

    private final NamespaceScorer scorer = new NamespaceScorer();

    private SecurityEvents eventInNamespace(String namespace) {
        return new SecurityEvents("any rule", "HIGH", namespace, "victim", Instant.now());
    }

    @Test
    void kubeSystemGivesCritical() {
        assertThat(scorer.score(eventInNamespace("kube-system"))).isEqualTo(Severity.CRITICAL);
    }

    @Test
    void falcoGivesCritical() {
        assertThat(scorer.score(eventInNamespace("falco"))).isEqualTo(Severity.CRITICAL);
    }

    @Test
    void sentinelGivesCritical() {
        assertThat(scorer.score(eventInNamespace("sentinel"))).isEqualTo(Severity.CRITICAL);
    }

    @Test
    void demoGivesMedium() {
        assertThat(scorer.score(eventInNamespace("demo"))).isEqualTo(Severity.MEDIUM);
    }

    @Test
    void defaultNamespaceGivesLow() {
        assertThat(scorer.score(eventInNamespace("default"))).isEqualTo(Severity.LOW);
    }
}