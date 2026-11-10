package dev.wiame.sentinel_api.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import dev.wiame.sentinel_api.repository.EventRepository;
import dev.wiame.sentinel_api.entity.SecurityEventEntity;
import dev.wiame.sentinel_api.model.SecurityEvents;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository){
        this.repository= repository;
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
    public SecurityEvents toRecord(SecurityEventEntity e){
        return new SecurityEvents(
                e.getRuleName(),
                e.getPriority(),
                e.getNamespace(),
                e.getPod(),
                e.getEventTime());
    }
}