package dev.wiame.sentinel_api.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;

import dev.wiame.sentinel_api.adapter.FalcoAlertAdapter;
import dev.wiame.sentinel_api.entity.IncidentEntity;
import dev.wiame.sentinel_api.entity.SecurityEventEntity;
import dev.wiame.sentinel_api.model.IncidentStatus;
import dev.wiame.sentinel_api.model.ScoredEvent;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;
import dev.wiame.sentinel_api.repository.EventRepository;
import dev.wiame.sentinel_api.repository.IncidentRepository;

class PersistObserverTest {

    private EventRepository eventRepository;
    private IncidentRepository incidentRepository;
    private PersistObserver observer;
    private String json;
    private SecurityEvents event;

    @BeforeEach
    void setUp() throws Exception {
        eventRepository = mock(EventRepository.class);
        incidentRepository = mock(IncidentRepository.class);
        observer = new PersistObserver(eventRepository, incidentRepository);

        // The mocked entity stands in for "a saved row that has an id".
        SecurityEventEntity saved = mock(SecurityEventEntity.class);
        when(saved.getId()).thenReturn(42L);
        when(eventRepository.save(any())).thenReturn(saved);

        json = new String(
                getClass().getResourceAsStream("/falco-shell.json").readAllBytes(),
                StandardCharsets.UTF_8);
        event = new FalcoAlertAdapter().adapt(json);
    }

    @ParameterizedTest
    @EnumSource(value = Severity.class, names = { "HIGH", "CRITICAL" })
    void highAndCriticalEventsCreateAnIncident(Severity severity) {
        observer.onEvent(new ScoredEvent(event, severity, json));

        ArgumentCaptor<IncidentEntity> captor = ArgumentCaptor.forClass(IncidentEntity.class);
        verify(incidentRepository).save(captor.capture());
        assertEquals(42L, captor.getValue().getEventId());
        assertEquals(severity, captor.getValue().getSeverity());
        assertEquals(IncidentStatus.OPEN, captor.getValue().getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = Severity.class, names = { "LOW", "MEDIUM" })
    void lowAndMediumEventsCreateNoIncident(Severity severity) {
        observer.onEvent(new ScoredEvent(event, severity, json));

        verify(eventRepository).save(any());
        verify(incidentRepository, never()).save(any());
    }
}