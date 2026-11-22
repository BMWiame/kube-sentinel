package dev.wiame.sentinel_api.observer;

import org.springframework.stereotype.Component;

import dev.wiame.sentinel_api.entity.IncidentEntity;
import dev.wiame.sentinel_api.entity.SecurityEventEntity;
import dev.wiame.sentinel_api.model.ScoredEvent;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;
import dev.wiame.sentinel_api.repository.EventRepository;
import dev.wiame.sentinel_api.repository.IncidentRepository;

@Component
public class PersistObserver implements EventObserver {
    private final EventRepository eventRepository;
    private final IncidentRepository incidentRepository;

    public PersistObserver(EventRepository eventRepository, IncidentRepository incidentRepository) {
        this.eventRepository = eventRepository;
        this.incidentRepository=incidentRepository;
    }

    @Override
    public void onEvent(ScoredEvent scoredEvent) {
        SecurityEvents event = scoredEvent.event();
        SecurityEventEntity entity = new SecurityEventEntity(
                event.rule(),
                event.priority(),
                event.namespace(),
                event.pod(),
                event.time(),
                scoredEvent.severity());
        entity.setRawJson(scoredEvent.rawJson());
        SecurityEventEntity saved = eventRepository.save(entity);
        Severity severity = scoredEvent.severity();
        if(severity==Severity.HIGH|| severity==Severity.CRITICAL){
            IncidentEntity incident = new IncidentEntity(saved.getId(), severity);
            incidentRepository.save(incident);
        }
    }
}