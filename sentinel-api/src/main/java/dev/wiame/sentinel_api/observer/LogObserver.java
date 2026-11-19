package dev.wiame.sentinel_api.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import dev.wiame.sentinel_api.model.ScoredEvent;
import dev.wiame.sentinel_api.model.SecurityEvents;

@Component
public class LogObserver implements EventObserver {
    private static final Logger log = LoggerFactory.getLogger(LogObserver.class);

    @Override
    public void onEvent(ScoredEvent scoredEvent) {
        SecurityEvents event = scoredEvent.event();
        log.info("rule={} namespace={} pod={} severity={}",
                event.rule(), event.namespace(), event.pod(), scoredEvent.severity());
    }
}