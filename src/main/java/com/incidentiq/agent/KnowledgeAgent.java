package com.incidentiq.agent;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeAgent {

    private final VectorStore vectorStore;

    public KnowledgeAgent(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String retrieveKnowledge(String query) {

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(5)
                .similarityThreshold(0.5)
                .filterExpression(
                        "knowledgeBase == 'incidentiq' && " +
                        "documentType == 'knowledge'")
                .build();

       List<Document> documents =
        vectorStore.similaritySearch(request);

if (documents.isEmpty()) {
    return "No relevant project knowledge found.";
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