package com.incidentiq.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class IncidentTools {

    @Tool(
        description = "Get the current health status of a backend service"
    )
    public String getServiceStatus(String serviceName) {

        if ("payment-service".equalsIgnoreCase(serviceName)) {
            return """
                    service: payment-service
                    status: UNHEALTHY
                    health: DOWN
                    """;
        }

        return "service: " + serviceName +
                "\nstatus: UNKNOWN";
    }

    @Tool(
        description = "Get the most recent logs for a backend service"
    )
    public String getRecentLogs(String serviceName) {

        if ("payment-service".equalsIgnoreCase(serviceName)) {
            return """
                    service: payment-service

                    recent logs:
                    ERROR Connection refused to PostgreSQL at db:5432
                    ERROR Database connection could not be established
                    WARN Health check failed
                    """;
        }

        return "No recent logs available for " + serviceName;
    }

    @Tool(
        description = "Get deployment information including version and deployment time"
    )
    public String getDeploymentInfo(String serviceName) {

        if ("payment-service".equalsIgnoreCase(serviceName)) {
            return """
                    service: payment-service
                    version: v2.4
                    deployed: 15 minutes ago
                    """;
        }

        return "No deployment information available for "
                + serviceName;
    }
}