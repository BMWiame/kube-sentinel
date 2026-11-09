package dev.wiame.sentinel_api.model;
import java.time.Instant;

public record SecurityEvents(String rule, String priority,String namespace, String pod, Instant time) {}