package com.incidentiq.controller;

import com.incidentiq.orchestrator.IncidentOrchestrator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class IncidentViewController {

    private final IncidentOrchestrator orchestrator;

    public IncidentViewController(IncidentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/ui/investigate")
    public String investigate(
            @RequestParam String incident,
            Model model) {

        if (incident == null || incident.isBlank()) {
            model.addAttribute("error", "Please enter an incident description.");
            return "index";
        }

        String result = orchestrator.investigate(incident);

        model.addAttribute("incident", incident);
        model.addAttribute("result", result);

        return "index";
    }
}