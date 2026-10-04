package com.incidentiq.agent;

import com.incidentiq.tools.IncidentTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class DiagnosisAgent {

    private final ChatClient chatClient;
    private final IncidentTools incidentTools;

    public DiagnosisAgent(
            ChatClient.Builder builder,
            IncidentTools incidentTools) {

        this.chatClient = builder.build();
        this.incidentTools = incidentTools;
    }

    public String investigate(String incident) {

        return chatClient
                .prompt()
                .system("""
                        You are the Diagnosis Agent for IncidentIQ.

                        Your responsibility is to gather current runtime
                        evidence about the reported incident.

                        Use the available tools when runtime information
                        is required.

                        Do not invent runtime information.

                        Return:
                        - Current status
                        - Relevant logs
                        - Deployment information
                        - Important observations

                        Do not claim that the evidence proves a root cause.
                        """)
                .user(incident)
                .tools(incidentTools)
                .call()
                .content();
    }
}