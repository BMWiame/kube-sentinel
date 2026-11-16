package dev.wiame.sentinel_api.observer;

import org.springframework.stereotype.Component;

import dev.wiame.sentinel_api.entity.SecurityEventEntity;
import dev.wiame.sentinel_api.model.IngestedEvent;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.repository.EventRepository;

@Component
public class PersistObserver implements EventObserver {
    private final EventRepository eventRepository;

    public PersistObserver(EventRepository eventRepository){
        this.eventRepository= eventRepository;
    }
    @Override
    public void onEvent(IngestedEvent ingestedEvent){
        SecurityEvents event = ingestedEvent.event();
        SecurityEventEntity entity = new SecurityEventEntity(
                event.rule(),
                event.priority(),
                event.namespace(),
                event.pod(),
                event.time());
        entity.setRawJson(ingestedEvent.rawJson());
        eventRepository.save(entity);}
        }
