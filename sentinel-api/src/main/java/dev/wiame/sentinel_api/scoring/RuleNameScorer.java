package dev.wiame.sentinel_api.scoring;

import java.util.Locale;

import org.springframework.stereotype.Component;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;

@Component("rule")
public class RuleNameScorer implements SeverityScorer{
    @Override
    public Severity score(SecurityEvents event){
        String rule = event.rule().toLowerCase(Locale.ROOT);
        if(rule.contains("shadow") || rule.contains("sensitive")){
            return Severity.CRITICAL;
        }
        else if(rule.contains("shell")){
            return Severity.HIGH;
        }
        else if(rule.contains("package")){
            return Severity.MEDIUM;
        }
        else{
            return Severity.LOW;
        }

    }
}
