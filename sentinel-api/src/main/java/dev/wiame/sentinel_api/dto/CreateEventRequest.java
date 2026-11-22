package dev.wiame.sentinel_api.dto; 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateEventRequest(
    @NotBlank String rule,
    @NotBlank @Pattern(regexp= "INFO|NOTICE|WARNING|CRITICAL") String priority,
    @NotBlank String namespace,
    @NotBlank String pod) {}
    
