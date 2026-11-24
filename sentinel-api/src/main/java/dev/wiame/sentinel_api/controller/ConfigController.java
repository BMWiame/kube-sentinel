package dev.wiame.sentinel_api.controller;

import dev.wiame.sentinel_api.dto.ScoringModeRequest;
import dev.wiame.sentinel_api.config.SentinelConfig;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;


@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private static final Set<String> VALID_MODES = Set.of("rule", "namespace", "highest");

    @PostMapping("/scoring-mode")
    public ScoringModeRequest setScoringMode(@RequestBody ScoringModeRequest request) {
        String mode = request.mode();

        if (mode == null || !VALID_MODES.contains(mode)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "mode must be one of: rule, namespace, highest");
        }

        SentinelConfig.getInstance().setScoringMode(mode);
        return request;
    }
}