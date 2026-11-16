package dev.wiame.sentinel_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dev.wiame.sentinel_api.adapter.FalcoAlertAdapter;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.repository.EventRepository;
import dev.wiame.sentinel_api.observer.EventSubject;
class EventServiceTest {

    @Test
    void addSavesAndReturnsRecord() {
        FalcoAlertAdapter adapter = new FalcoAlertAdapter();
        EventRepository repository = Mockito.mock(EventRepository.class);
        EventSubject subject = Mockito.mock(EventSubject.class);
        Mockito.when(repository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));
        EventService service = new EventService(repository, adapter, subject);

        SecurityEvents created = service.add("Terminal shell in container", "NOTICE", "demo", "victim-1");

        assertEquals("Terminal shell in container", created.rule());
        assertNotNull(created.time());
        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    void ingestSavesTheRealShellAlertOnce() throws Exception {
        EventRepository repository = Mockito.mock(EventRepository.class);
        EventSubject subject = Mockito.mock(EventSubject.class);
        EventService service = new EventService(repository, new FalcoAlertAdapter(), subject);
        String json = new String(
                getClass().getResourceAsStream("/falco-shell.json").readAllBytes(),
                StandardCharsets.UTF_8);

        service.ingest(json);

        Mockito.verify(subject).publish(Mockito.any());
    }

    @Test
    void ingestNeverSavesAlertWithoutNamespace() {
        EventRepository repository = Mockito.mock(EventRepository.class);
        EventSubject subject = Mockito.mock(EventSubject.class);
        EventService service = new EventService(repository, new FalcoAlertAdapter(), subject);

        service.ingest("{\"rule\":\"x\"}");

        Mockito.verify(subject, Mockito.never()).publish(Mockito.any());}
    }