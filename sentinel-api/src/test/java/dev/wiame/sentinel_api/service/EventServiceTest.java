package dev.wiame.sentinel_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import dev.wiame.sentinel_api.adapter.FalcoAlertAdapter;
import dev.wiame.sentinel_api.model.ScoredEvent;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.model.Severity;
import dev.wiame.sentinel_api.observer.EventSubject;
import dev.wiame.sentinel_api.repository.EventRepository;
import dev.wiame.sentinel_api.scoring.ScoringService;

class EventServiceTest {

    private String shellJson() throws Exception {
        return new String(
                getClass().getResourceAsStream("/falco-shell.json").readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void addSavesAndReturnsRecord() {
        FalcoAlertAdapter adapter = new FalcoAlertAdapter();
        EventRepository repository = Mockito.mock(EventRepository.class);
        EventSubject subject = Mockito.mock(EventSubject.class);
        ScoringService scoringService = Mockito.mock(ScoringService.class);
        Mockito.when(repository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));
        EventService service = new EventService(repository, adapter, subject, scoringService);

        SecurityEvents created = service.add("Terminal shell in container", "NOTICE", "demo", "victim-1");

        assertEquals("Terminal shell in container", created.rule());
        assertNotNull(created.time());
        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    void ingestPublishesShellAlertWithScoredSeverity() throws Exception {
        EventRepository repository = Mockito.mock(EventRepository.class);
        EventSubject subject = Mockito.mock(EventSubject.class);
        ScoringService scoringService = Mockito.mock(ScoringService.class);
        Mockito.when(scoringService.score(Mockito.any())).thenReturn(Severity.HIGH);
        EventService service = new EventService(repository, new FalcoAlertAdapter(), subject, scoringService);
        String json = shellJson();

        ScoredEvent result = service.ingest(json, true);

        ArgumentCaptor<ScoredEvent> captor = ArgumentCaptor.forClass(ScoredEvent.class);
        Mockito.verify(subject).publish(captor.capture());
        assertEquals(Severity.HIGH, captor.getValue().severity());
        assertEquals(json, captor.getValue().rawJson());
        assertEquals(captor.getValue(), result);
    }

    @Test
    void ingestNeverPublishesAlertWithoutNamespace() {
        EventRepository repository = Mockito.mock(EventRepository.class);
        EventSubject subject = Mockito.mock(EventSubject.class);
        ScoringService scoringService = Mockito.mock(ScoringService.class);
        EventService service = new EventService(repository, new FalcoAlertAdapter(), subject, scoringService);

        ScoredEvent result = service.ingest("{\"rule\":\"x\"}", true);

        assertNull(result);
        Mockito.verify(subject, Mockito.never()).publish(Mockito.any());
        Mockito.verifyNoInteractions(scoringService);
    }

    @Test
    void dedupeTrueDropsSecondIdenticalAlert() throws Exception {
        EventRepository repository = Mockito.mock(EventRepository.class);
        EventSubject subject = Mockito.mock(EventSubject.class);
        ScoringService scoringService = Mockito.mock(ScoringService.class);
        Mockito.when(scoringService.score(Mockito.any())).thenReturn(Severity.HIGH);
        EventService service = new EventService(repository, new FalcoAlertAdapter(), subject, scoringService);
        String json = shellJson();

        ScoredEvent first = service.ingest(json, true);
        ScoredEvent second = service.ingest(json, true);

        assertNotNull(first);
        assertNull(second);
        Mockito.verify(subject, times(1)).publish(Mockito.any());
    }

    @Test
    void dedupeFalsePublishesBothAlerts() throws Exception {
        EventRepository repository = Mockito.mock(EventRepository.class);
        EventSubject subject = Mockito.mock(EventSubject.class);
        ScoringService scoringService = Mockito.mock(ScoringService.class);
        Mockito.when(scoringService.score(Mockito.any())).thenReturn(Severity.HIGH);
        EventService service = new EventService(repository, new FalcoAlertAdapter(), subject, scoringService);
        String json = shellJson();

        service.ingest(json, false);
        service.ingest(json, false);

        Mockito.verify(subject, times(2)).publish(Mockito.any());
    }
}