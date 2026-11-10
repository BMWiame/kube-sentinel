package dev.wiame.sentinel_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.repository.EventRepository;

class EventServiceTest {

    @Test
    void addSavesAndReturnsRecord() {
        EventRepository repository = Mockito.mock(EventRepository.class);
        Mockito.when(repository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));
        EventService service = new EventService(repository);

        SecurityEvents created = service.add("Terminal shell in container", "NOTICE", "demo", "victim-1");

        assertEquals("Terminal shell in container", created.rule());
        assertNotNull(created.time());
        Mockito.verify(repository).save(Mockito.any());
    }
}