package dev.wiame.sentinel_api.observer;

import org.springframework.stereotype.Component;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.IngestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LogObserver implements EventObserver {
    private static final Logger log = LoggerFactory.getLogger(LogObserver.class);
    @Override
    public void onEvent(IngestedEvent ingestedEvent){
        SecurityEvents event = ingestedEvent.event();
        log.info("rule={} namespace={} pod={}",event.rule(), event.namespace(), event.pod());


    }
}
