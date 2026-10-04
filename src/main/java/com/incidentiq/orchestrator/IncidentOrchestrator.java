package com.incidentiq.orchestrator;

import com.incidentiq.agent.DiagnosisAgent;
import com.incidentiq.agent.HistoryAgent;
import com.incidentiq.agent.KnowledgeAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class IncidentOrchestrator {

    private final KnowledgeAgent knowledgeAgent;
    private final DiagnosisAgent diagnosisAgent;
    private final HistoryAgent historyAgent;
    private final ChatClient chatClient;

    public IncidentOrchestrator(
            KnowledgeAgent knowledgeAgent,
            DiagnosisAgent diagnosisAgent,
            HistoryAgent historyAgent,
            ChatClient.Builder builder) {

        this.knowledgeAgent = knowledgeAgent;
        this.diagnosisAgent = diagnosisAgent;
        this.historyAgent = historyAgent;
        this.chatClient = builder.build();
    }

    public String investigate(String incident) {

    String knowledge;
    String runtimeEvidence;
    String historicalEvidence;

    try {
        knowledge =
                knowledgeAgent.retrieveKnowledge(incident);
    } catch (Exception e) {
        knowledge = "Knowledge retrieval failed.";
    }

    try {
        runtimeEvidence =
                diagnosisAgent.investigate(incident);
    } catch (Exception e) {
        runtimeEvidence = "Current runtime evidence unavailable.";
    }

    try {
        historicalEvidence =
                historyAgent.findSimilarIncidents(incident);
    } catch (Exception e) {
        historicalEvidence = "Historical incident retrieval failed.";
    }

    String finalPrompt = """
            You are IncidentIQ, an AI incident response copilot.

            Investigate the incident using ONLY the evidence
            supplied below.

            INCIDENT:
            %s

            PROJECT KNOWLEDGE:
            %s

            CURRENT RUNTIME EVIDENCE:
            %s

            HISTORICAL INCIDENT EVIDENCE:
            %s

            Produce a structured investigation report.

            Use exactly these sections:

            1. Evidence
            2. Likely Cause
            3. Confidence
            4. Uncertainty
            5. Recommended Actions

            Important rules:

            - Do not claim certainty without direct evidence.
            - Clearly distinguish evidence from inference.
            - Do not invent missing metrics, logs, or system state.
            - Historical incidents are supporting evidence only.
            - If evidence is insufficient, explicitly say so.
            """.formatted(
            incident,
            knowledge,
            runtimeEvidence,
            historicalEvidence);

    return chatClient
            .prompt()
            .user(finalPrompt)
            .call()
            .content();
}
}