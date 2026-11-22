package dev.wiame.sentinel_api.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.wiame.sentinel_api.model.ScoredEvent;
import dev.wiame.sentinel_api.service.EventService;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
public class SimulatorController {
    private final EventService service;
    private final List<String> samples = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();

    public SimulatorController(EventService service) throws IOException {
        this.service = service;
        for (Resource resource : new PathMatchingResourcePatternResolver()
                .getResources("classpath:samples/*.json")) {
            samples.add(resource.getContentAsString(StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/api/simulate")
    public ScoredEvent simulate() {
        String json = samples.get(ThreadLocalRandom.current().nextInt(samples.size()));
        ObjectNode node = (ObjectNode) mapper.readTree(json);
        node.put("time", Instant.now().toString());
        return service.ingest(mapper.writeValueAsString(node), false);
    }
}