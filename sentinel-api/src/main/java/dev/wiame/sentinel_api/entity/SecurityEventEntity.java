package dev.wiame.sentinel_api.entity;

import java.time.Instant;

import dev.wiame.sentinel_api.model.Severity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "security_events")
public class SecurityEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ruleName;
    private String priority;
    private String namespace;
    private String pod;
    private Instant eventTime;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(columnDefinition = "text")
    private String rawJson;

    protected SecurityEventEntity() {}

    public SecurityEventEntity(String ruleName, String priority, String namespace,
                               String pod, Instant eventTime, Severity severity) {
        this.ruleName = ruleName;
        this.priority = priority;
        this.namespace = namespace;
        this.pod = pod;
        this.eventTime = eventTime;
        this.severity = severity;
    }

    public Long getId() {
        return id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getPriority() {
        return priority;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPod() {
        return pod;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getRawJson() {
        return rawJson;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }
}