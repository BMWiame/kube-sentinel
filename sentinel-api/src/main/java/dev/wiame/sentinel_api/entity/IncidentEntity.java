package dev.wiame.sentinel_api.entity;

import java.time.Instant;

import dev.wiame.sentinel_api.model.IncidentStatus;
import dev.wiame.sentinel_api.model.Severity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "incidents")
public class IncidentEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long eventId;
    @Enumerated(EnumType.STRING)
    private Severity severity;
    @Enumerated(EnumType.STRING)
    private IncidentStatus status;
    private Instant createdAt;
    
    protected IncidentEntity() {}

    public IncidentEntity(Long eventId, Severity severity) {
        this.eventId = eventId;
        this.severity = severity;
        this.status = IncidentStatus.OPEN;
        this.createdAt = Instant.now();
    }
    
    public Long getId(){
        return id;
    }
    public Long getEventId(){
        return eventId;
    }

    public Severity getSeverity(){
        return severity;
    }

    public IncidentStatus getStatus(){
        return status;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }
}
