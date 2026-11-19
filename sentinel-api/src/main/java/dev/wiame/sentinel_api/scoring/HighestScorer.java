package dev.wiame.sentinel_api.scoring;

import org.springframework.stereotype.Component;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;

@Component("highest")
public class HighestScorer implements SeverityScorer {

    private final RuleNameScorer ruleScorer;
    private final NamespaceScorer namespaceScorer;

    public HighestScorer(RuleNameScorer ruleScorer, NamespaceScorer namespaceScorer) {
        this.ruleScorer = ruleScorer;
        this.namespaceScorer = namespaceScorer;
    }

    @Override
    public Severity score(SecurityEvents event) {
        Severity ruleSeverity = ruleScorer.score(event);
        Severity namespaceSeverity = namespaceScorer.score(event);
        return ruleSeverity.ordinal() >= namespaceSeverity.ordinal()
                ? ruleSeverity
                : namespaceSeverity;
    }
}