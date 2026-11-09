package dev.wiame.sentinel_api.controller;
import dev.wiame.sentinel_api.model.SecurityEvents;
import dev.wiame.sentinel_api.service.EventService;
import java.util.Map;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
;

@RestController
@RequestMapping ("/api")
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
    public Map<String,String> health(){
        return Map.of("status", "UP");
    }
}
