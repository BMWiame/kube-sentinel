package dev.wiame.sentinel_api.scoring;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;

class RuleNameScorerTest {

    private final RuleNameScorer scorer = new RuleNameScorer();

    private SecurityEvents eventWithRule(String rule) {
        return new SecurityEvents(rule, "HIGH", "demo", "victim", Instant.now());
    }

    @Test
    void shellGivesHigh() {
        assertThat(scorer.score(eventWithRule("Terminal shell in container")))
                .isEqualTo(Severity.HIGH);
    }

    @Test
    void sensitiveGivesCritical() {
        assertThat(scorer.score(eventWithRule("Read sensitive file untrusted")))
                .isEqualTo(Severity.CRITICAL);
    }

    @Test
    void packageGivesMedium() {
        assertThat(scorer.score(eventWithRule("Launch Package Management Process")))
                .isEqualTo(Severity.MEDIUM);
    }

    @Test
    void unknownGivesLow() {
        assertThat(scorer.score(eventWithRule("Something unrelated")))
                .isEqualTo(Severity.LOW);
    }

    @Test
    void matchingIgnoresCase() {
        assertThat(scorer.score(eventWithRule("TERMINAL SHELL IN CONTAINER")))
                .isEqualTo(Severity.HIGH);
    }
}