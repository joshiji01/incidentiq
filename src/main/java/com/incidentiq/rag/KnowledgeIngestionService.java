package com.incidentiq.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KnowledgeIngestionService {

    private static final int CHUNK_SIZE = 1200;
    private static final int OVERLAP = 200;

    private final VectorStore vectorStore;
    private final ResourcePatternResolver resolver;

    public KnowledgeIngestionService(
            VectorStore vectorStore,
            ResourcePatternResolver resolver) {

        this.vectorStore = vectorStore;
        this.resolver = resolver;
    }

    public void ingestKnowledge() throws IOException {

        vectorStore.delete("knowledgeBase == 'incidentiq'");

        List<Document> documents = new ArrayList<>();

        documents.addAll(loadDirectory(
                "classpath:/docs/*.md",
                "knowledge"));

        documents.addAll(loadDirectory(
                "classpath:/incidents/*.md",
                "incident"));

        vectorStore.add(documents);

        System.out.println(
                "Indexed " + documents.size() + " chunks into pgvector.");
    }

    private List<Document> loadDirectory(
            String pattern,
            String documentType) throws IOException {

        Resource[] resources = resolver.getResources(pattern);

        List<Document> chunks = new ArrayList<>();

        for (Resource resource : resources) {

            String content = new String(
                    resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);

            String source = resource.getFilename();

            chunks.addAll(chunk(
                    content,
                    source,
                    documentType));
        }

        return chunks;
    }

    private List<Document> chunk(
            String content,
            String source,
            String documentType) {

        List<Document> documents = new ArrayList<>();

        int start = 0;

        while (start < content.length()) {

            int end = Math.min(
                    start + CHUNK_SIZE,
                    content.length());

            String chunkText =
                    content.substring(start, end).trim();

            if (!chunkText.isBlank()) {

                Map<String, Object> metadata =
                        new HashMap<>();

                metadata.put("source", source);
                metadata.put("documentType", documentType);
                metadata.put("knowledgeBase", "incidentiq");

                documents.add(
                        new Document(chunkText, metadata));
            }

            if (end == content.length()) {
                break;
            }

            start = end - OVERLAP;
        }

        return documents;
    }
}