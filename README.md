# IncidentIQ — AI Incident Response Copilot

IncidentIQ is an AI-powered incident investigation system built with Spring Boot and Spring AI.

It helps developers investigate backend incidents by combining:

- Project and operational knowledge using RAG
- Current runtime evidence using tool calling
- Historical incident retrieval
- LLM-based evidence synthesis

The system is designed as a copilot rather than an autonomous root-cause system. It distinguishes evidence from inference and explicitly reports uncertainty when the available evidence is insufficient.

---

## Problem

When a production service starts failing, developers usually need to investigate multiple sources:

- Architecture and service documentation
- Runbooks
- Current service health
- Recent application logs
- Deployment information
- Previous incidents

IncidentIQ brings these sources together and produces a structured investigation report.

Example:

> Payment Service started returning 503 errors after deployment v2.4. Investigate.

IncidentIQ retrieves relevant knowledge, gathers current runtime evidence, searches historical incidents, and asks the LLM to synthesize the evidence.

---

## Architecture

```text
                         Client
                           |
                           v
                  Incident Controller
                           |
                           v
                 Incident Orchestrator
                           |
          +----------------+----------------+
          |                |                |
          v                v                v
   Knowledge Agent   Diagnosis Agent   History Agent
          |                |                |
          v                v                v
       PGVector         Tool Calls       PGVector
          |                |                |
          |         +------+-------+         |
          |         |      |       |         |
          |       Status  Logs  Deployment   |
          |                                  |
          +----------------+-----------------+
                           |
                           v
                    Evidence Aggregation
                           |
                           v
                      Gemini LLM
                           |
                           v
                 Investigation Report
Core Components
1. Knowledge Agent

Retrieves relevant project knowledge using vector similarity search.

Sources include:

Architecture documentation
Service documentation
Deployment guides
Database runbooks

The retrieved documents are stored in PostgreSQL with pgvector.

2. Diagnosis Agent

Responsible for gathering current runtime evidence.

It uses Spring AI tool calling to access:

Service health/status
Recent logs
Deployment information

The current MVP uses controlled/mock runtime data. In a production environment, these tools could be connected to real observability or infrastructure APIs.

3. History Agent

Searches historical incidents using vector similarity search.

This allows IncidentIQ to identify previously observed failure patterns and use them as supporting evidence.

Historical incidents are not treated as proof of the current root cause.

4. Incident Orchestrator

The orchestrator coordinates the investigation workflow.

It:

Retrieves project knowledge
Gathers current runtime evidence
Retrieves similar historical incidents
Combines the evidence
Sends the evidence to Gemini
Produces the final investigation report

The current implementation uses a deterministic workflow where the three agents are executed sequentially.

RAG Pipeline

IncidentIQ uses Retrieval-Augmented Generation to ground the LLM with project-specific information.

Markdown Documents
       |
       v
Document Loading
       |
       v
Chunking
       |
       v
Metadata
       |
       v
Gemini Embeddings
       |
       v
PostgreSQL + pgvector
       |
       v
Similarity Search
       |
       v
Top-K Relevant Chunks
       |
       v
LLM Context

Documents are divided into chunks before being converted into embeddings.

For an investigation query, the query is also converted into an embedding.

pgvector performs similarity search and returns the most relevant chunks.

The retrieved text is then supplied to Gemini as context.

Tool Calling

RAG provides information about what is already known.

Tool calling provides information about what is happening now.

IncidentIQ exposes the following tools:

getServiceStatus(serviceName)
getRecentLogs(serviceName)
getDeploymentInfo(serviceName)

The flow is:

Gemini
   |
   | Tool Request
   v
Spring AI
   |
   v
Java Tool Method
   |
   v
Tool Result
   |
   v
Gemini

The LLM does not directly execute Java code. Spring AI maps the model's tool request to the corresponding Java method.

Agent Responsibilities
Agent	Responsibility	Data Source
Knowledge Agent	Project/service knowledge	PGVector
Diagnosis Agent	Current runtime evidence	Tools
History Agent	Previous incidents	PGVector
Orchestrator	Coordinates investigation	All agents

The agents can use the same Gemini model while having different responsibilities, prompts, retrieval scopes, and tools.

Knowledge Base

The current knowledge base contains:

src/main/resources/
├── docs/
│   ├── architecture.md
│   ├── payment-service.md
│   ├── deployment-guide.md
│   └── database-runbook.md
│
└── incidents/
    ├── incident-001.md
    ├── incident-002.md
    └── incident-003.md

Metadata is used to distinguish knowledge documents from historical incidents.

Investigation API
Endpoint
POST /api/incidents/investigate
Request
{
  "incident": "Payment Service started returning 503 errors after deployment v2.4. Investigate."
}
Example Response
1. Evidence

- payment-service is currently unhealthy
- PostgreSQL connection is being refused
- version v2.4 was deployed approximately 15 minutes ago
- similar database connectivity failures occurred previously

2. Likely Cause

The most likely cause is a database connectivity failure.

3. Confidence

High

4. Uncertainty

The available evidence does not conclusively determine whether
the problem is caused by configuration, networking, credentials,
or PostgreSQL availability.

5. Recommended Actions

- Verify database connectivity
- Check database connection configuration
- Verify credentials
- Check network connectivity
- Compare the deployment configuration with the previous version
- Consider rollback if the issue started immediately after deployment

Technology Stack
Java 21
Spring Boot
Spring AI
Google Gemini
PostgreSQL
pgvector
Spring Data / VectorStore
Maven
Docker / Docker Compose
REST API

Project Structure
src/main/java/com/incidentiq/
│
├── agent/
│   ├── DiagnosisAgent.java
│   ├── HistoryAgent.java
│   └── KnowledgeAgent.java
│
├── controller/
│   ├── ChatController.java
│   ├── IncidentController.java
│   └── IncidentRequest.java
│
├── orchestrator/
│   └── IncidentOrchestrator.java
│
├── rag/
│   ├── KnowledgeDataLoader.java
│   └── KnowledgeIngestionService.java
│
└── tools/
    └── IncidentTools.java

Running Locally:
1. Start PostgreSQL + pgvector
docker compose up -d

The PostgreSQL container is exposed on:

localhost:5433
2. Configure Gemini API Key

Set the API key as an environment variable.

Windows PowerShell:

$env:GEMINI_API_KEY="YOUR_API_KEY"

Do not commit API keys to Git.

3. Start the application

Using Maven:

./mvnw spring-boot:run

Windows:

.\mvnw.cmd spring-boot:run

The application runs on:

http://localhost:8080
Testing the Investigation API

Using PowerShell:

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/incidents/investigate" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"incident":"Payment Service started returning 503 errors after deployment v2.4. Investigate."}'
Design Principles
Evidence over assumptions

IncidentIQ does not automatically treat an inferred cause as confirmed.

It separates:

Evidence
Likely Cause
Confidence
Uncertainty
Recommended Actions
RAG vs Tools

RAG answers:

What do we already know about this system?

Tools answer:

What is happening in the system right now?

Historical RAG answers:

Have we seen something similar before?

Gemini combines these sources to produce the investigation report.

Future Improvements

Potential production improvements include:

Connect tools to real monitoring/logging systems
Add authentication and authorization
Add real-time metrics
Parallelize independent agent calls
Add incident persistence
Add alerting integrations
Add richer observability integrations
Add evaluation and automated quality checks
Author

Tarun Joshi
