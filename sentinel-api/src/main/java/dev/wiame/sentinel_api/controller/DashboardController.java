package dev.wiame.sentinel_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import dev.wiame.sentinel_api.config.SentinelConfig;
import dev.wiame.sentinel_api.service.EventService;

@Controller
public class DashboardController {

    private final EventService service;

    public DashboardController(EventService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String getView(Model model) {
        model.addAttribute("events", service.latest());
        model.addAttribute("mode", SentinelConfig.getInstance().getScoringMode());
        return "dashboard";
    }
}