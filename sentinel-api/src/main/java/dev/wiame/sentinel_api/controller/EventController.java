package dev.wiame.sentinel_api.controller;

import dev.wiame.sentinel_api.dto.CreateEventRequest;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.service.EventService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<SecurityEvents> getEvents() {
        return eventService.getEvents();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public SecurityEvents create(@Valid @RequestBody CreateEventRequest request) {
        SecurityEvents event = eventService.add(request.rule(), request.priority(), request.namespace(), request.pod());
        return event;
    }
    @PostMapping("/ingest/falco")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void ingestFalco(@RequestBody String json){
        eventService.ingest(json);
    }
}