package dev.wiame.sentinel_api.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import dev.wiame.sentinel_api.repository.EventRepository;
import dev.wiame.sentinel_api.entity.SecurityEventEntity;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.observer.EventSubject;
import dev.wiame.sentinel_api.adapter.FalcoAlertAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.wiame.sentinel_api.model.IngestedEvent;

@Service
public class EventService {
    private final EventRepository repository;
    private final FalcoAlertAdapter adapter;
    private final EventSubject subject;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    public EventService(EventRepository repository, FalcoAlertAdapter adapter, EventSubject subject){
        this.repository = repository;
        this.adapter = adapter;
        this.subject = subject;
    }

    public List<SecurityEvents> getEvents(){
        return repository.findAll().stream()
            .map(this::toRecord)
            .toList();
    }

    public SecurityEvents add(String rule, String priority, String namespace, String pod){
        SecurityEventEntity entity = new SecurityEventEntity(rule, priority, namespace, pod, Instant.now());
        SecurityEventEntity saved = repository.save(entity);
        return toRecord(saved);
    }

    public void ingest(String json){
        SecurityEvents event = adapter.adapt(json);
        IngestedEvent ingestedEvent = new IngestedEvent(event, json);
        String namespace= ingestedEvent.event().namespace();
        if(namespace!=null && !namespace.isBlank() && !namespace.equals("unknown")){
            subject.publish(ingestedEvent);
        }
        else{
            log.info("Ignored non-Kubernetes alert");
        }
    }

    public SecurityEvents toRecord(SecurityEventEntity e){
        return new SecurityEvents(
                e.getRuleName(),
                e.getPriority(),
                e.getNamespace(),
                e.getPod(),
                e.getEventTime());
    }
}