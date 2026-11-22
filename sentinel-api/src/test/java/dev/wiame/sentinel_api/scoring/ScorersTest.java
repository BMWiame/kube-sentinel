package dev.wiame.sentinel_api.scoring;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;

class ScorersTest {

    private final RuleNameScorer ruleScorer = new RuleNameScorer();
    private final NamespaceScorer namespaceScorer = new NamespaceScorer();
    private final HighestScorer highestScorer = new HighestScorer(ruleScorer, namespaceScorer);

    private SecurityEvents event(String rule, String namespace) {
        return new SecurityEvents(rule, "HIGH", namespace, "victim", Instant.now());
    }

    // ---- RuleNameScorer ----
    @Test
    void ruleShellIsHigh() {
        assertThat(ruleScorer.score(event("Terminal shell in container", "demo")))
                .isEqualTo(Severity.HIGH);
    }

    @Test
    void ruleSensitiveFileIsCritical() {
        assertThat(ruleScorer.score(event("Read sensitive file untrusted", "demo")))
                .isEqualTo(Severity.CRITICAL);
    }

    @Test
    void rulePackageIsMedium() {
        assertThat(ruleScorer.score(event("Launch Package Management Process in Container", "demo")))
                .isEqualTo(Severity.MEDIUM);
    }

    @Test
    void ruleUnrelatedIsLow() {
        assertThat(ruleScorer.score(event("Something unrelated", "demo")))
                .isEqualTo(Severity.LOW);
    }

    // ---- NamespaceScorer ----
    @Test
    void namespaceDemoIsMedium() {
        assertThat(namespaceScorer.score(event("x", "demo"))).isEqualTo(Severity.MEDIUM);
    }

    @Test
    void namespaceKubeSystemIsCritical() {
        assertThat(namespaceScorer.score(event("x", "kube-system"))).isEqualTo(Severity.CRITICAL);
    }

    // ---- HighestScorer ----
    @Test
    void highestPicksRuleWhenRuleIsHigher() {
        // rule HIGH vs namespace MEDIUM
        assertThat(highestScorer.score(event("Terminal shell in container", "demo")))
                .isEqualTo(Severity.HIGH);
    }

    @Test
    void highestPicksNamespaceWhenNamespaceIsHigher() {
        // rule LOW vs namespace CRITICAL
        assertThat(highestScorer.score(event("Something unrelated", "kube-system")))
                .isEqualTo(Severity.CRITICAL);
    }

    @Test
    void highestWhenBothAgree() {
        // rule CRITICAL vs namespace MEDIUM -> CRITICAL
        assertThat(highestScorer.score(event("Read sensitive file untrusted", "demo")))
                .isEqualTo(Severity.CRITICAL);
    }
}