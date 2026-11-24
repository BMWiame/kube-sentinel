package dev.wiame.sentinel_api.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.wiame.sentinel_api.adapter.FalcoAlertAdapter;
import dev.wiame.sentinel_api.entity.SecurityEventEntity;
import dev.wiame.sentinel_api.model.ScoredEvent;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;
import dev.wiame.sentinel_api.observer.EventSubject;
import dev.wiame.sentinel_api.repository.EventRepository;
import dev.wiame.sentinel_api.scoring.ScoringService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class EventService {
    private final EventRepository repository;
    private final FalcoAlertAdapter adapter;
    private final EventSubject subject;
    private final ScoringService scoringService;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private final Map<String, Instant> lastSeen = new ConcurrentHashMap<>();
    public EventService(EventRepository repository, FalcoAlertAdapter adapter,
                        EventSubject subject, ScoringService scoringService) {
        this.repository = repository;
        this.adapter = adapter;
        this.subject = subject;
        this.scoringService = scoringService;
    }

    public List<SecurityEvents> getEvents() {
        return repository.findAll().stream()
            .map(this::toRecord)
            .toList();
    }

    public SecurityEvents add(String rule, String priority, String namespace, String pod) {
        SecurityEventEntity entity =
                new SecurityEventEntity(rule, priority, namespace, pod, Instant.now(), null);
        SecurityEventEntity saved = repository.save(entity);
        return toRecord(saved);
    }

    public ScoredEvent ingest(String json, boolean dedupe) {
        SecurityEvents event = adapter.adapt(json);
        String namespace = event.namespace();

        if (namespace == null || namespace.isBlank() || namespace.equals("unknown")|| namespace.equals("null")) {
            log.info("Ignored non-Kubernetes alert");
            return null;
        }
        if(dedupe){
            String key =event.rule() + "|" + event.pod();
            Instant now = Instant.now();
            Instant previous = lastSeen.get(key);
            if(previous != null && Duration.between(previous, now).getSeconds()< 30){
                return null;
            }
            lastSeen.put(key, now);
        }
        Severity severity = scoringService.score(event);
        ScoredEvent scoredEvent = new ScoredEvent(event, severity, json);
        subject.publish(scoredEvent);
        return scoredEvent;
    }

    public SecurityEvents toRecord(SecurityEventEntity e) {
        return new SecurityEvents(
                e.getRuleName(),
                e.getPriority(),
                e.getNamespace(),
                e.getPod(),
                e.getEventTime());
    }
    public List<SecurityEventEntity> latest(){
        return repository.findTop50ByOrderByIdDesc();
    }
}