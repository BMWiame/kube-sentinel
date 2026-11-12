package dev.wiame.sentinel_api.adapter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dev.wiame.sentinel_api.model.SecurityEvents;

class FalcoAlertAdapterTest {

    @Test
    void adaptsTheRealShellAlert() throws Exception {
        
        String json = new String(
                getClass().getResourceAsStream("/falco-shell.json").readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8);
        FalcoAlertAdapter adapter = new FalcoAlertAdapter();

        
        SecurityEvents event = adapter.adapt(json);

        
        assertEquals("Terminal shell in container", event.rule());
        assertEquals("NOTICE", event.priority());
        assertEquals("demo", event.namespace());
        assertTrue(event.pod().startsWith("victim"));
        assertNotNull(event.time());
    }



    @Test
    void survivesMissingFields(){
        FalcoAlertAdapter adapter = new FalcoAlertAdapter();

        SecurityEvents event = adapter.adapt("{\"rule\":\"x\"}");

        assertEquals("x", event.rule());
        assertEquals("unknown", event.namespace());
        assertEquals("unknown", event.pod());
    }
    @Test
    void rejectsGarbage(){
        FalcoAlertAdapter adapter = new FalcoAlertAdapter();
        assertThrows(IllegalArgumentException.class,
                () -> adapter.adapt("this is not json"));
    }
}