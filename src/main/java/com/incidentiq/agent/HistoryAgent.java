package com.incidentiq.agent;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryAgent {

    private final VectorStore vectorStore;

    public HistoryAgent(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String findSimilarIncidents(String query) {

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(3)
                .similarityThreshold(0.5)
                .filterExpression(
                        "knowledgeBase == 'incidentiq' && " +
                        "documentType == 'incident'")
                .build();

        List<Document> documents =
                vectorStore.similaritySearch(request);

        if (documents.isEmpty()) {
            return "No similar historical incidents found.";
        }

        return documents.stream()
                .map(doc ->
                        "Source: " +
                        doc.getMetadata().get("source") +
                        "\n" +
                        doc.getText())
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}