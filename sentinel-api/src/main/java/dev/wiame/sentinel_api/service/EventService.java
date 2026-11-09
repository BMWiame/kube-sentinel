package dev.wiame.sentinel_api.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.wiame.sentinel_api.model.SecurityEvents;

@Service 
public class EventService {
    public List<SecurityEvents> getEvents(){
        return List.of(
            new SecurityEvents("Shell","INFO","idk","idk",Instant.now()),
            new SecurityEvents("idk", "CRITICAL", "idk", "idk", Instant.now()),
            new SecurityEvents("idk", "NORMAL", "idk", "idk", Instant.now())
            );
        
}
}

