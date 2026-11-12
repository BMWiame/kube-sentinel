package dev.wiame.sentinel_api.adapter;

import java.time.Instant;

import org.springframework.stereotype.Component;

import dev.wiame.sentinel_api.model.SecurityEvents;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class FalcoAlertAdapter {

    private static final String UNKNOWN = "unknown";

    private final ObjectMapper mapper = new ObjectMapper();

    public SecurityEvents adapt(String json) {
        JsonNode tree;
        try {
            tree = mapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Falco JSON", e);
        }

        JsonNode fields = tree.path("output_fields");

        String rule = text(tree.path("rule"), UNKNOWN);
        String priority = text(tree.path("priority"), UNKNOWN).toUpperCase();
        String namespace = text(fields.path("k8s.ns.name"), UNKNOWN);
        String pod = text(fields.path("k8s.pod.name"), UNKNOWN);

        String timeText = text(tree.path("time"), "");
        Instant time = timeText.isEmpty() ? Instant.now() : Instant.parse(timeText);

        return new SecurityEvents(rule, priority, namespace, pod, time);
    }

    private String text(JsonNode node, String fallback) {
        return (node.isMissingNode() || node.isNull()) ? fallback : node.asString();
    }
}