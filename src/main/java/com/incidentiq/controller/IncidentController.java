package com.incidentiq.controller;

import com.incidentiq.orchestrator.IncidentOrchestrator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentOrchestrator orchestrator;

    public IncidentController(IncidentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/investigate")
    public String investigate(@RequestBody IncidentRequest request) {

        if (request.incident() == null ||
                request.incident().isBlank()) {
            return "Incident description cannot be empty.";
        }

        return orchestrator.investigate(request.incident());
    }
}