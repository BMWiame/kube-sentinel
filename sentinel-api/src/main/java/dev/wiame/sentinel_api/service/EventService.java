package dev.wiame.sentinel_api.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import dev.wiame.sentinel_api.repository.EventRepository;
import dev.wiame.sentinel_api.entity.SecurityEventEntity;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.adapter.FalcoAlertAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class EventService {
    private final EventRepository repository;
    private final FalcoAlertAdapter adapter;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    public EventService(EventRepository repository,FalcoAlertAdapter adapter){
        this.repository= repository;
        this.adapter= adapter;
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
        String namespace=event.namespace();
        if(namespace.isBlank() || namespace == null || namespace.equals("unknown")){
            log.info("Ignoredd non-Kubernetes alert");
            return;
        }
        SecurityEventEntity entity = new SecurityEventEntity(event.rule(), event.priority(), event.namespace(), event.pod(), event.time());
        
        entity.setRawJson(json);
        repository.save(entity);
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