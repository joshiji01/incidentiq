package com.incidentiq.rag;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeDataLoader implements CommandLineRunner {

    private final KnowledgeIngestionService ingestionService;

    public KnowledgeDataLoader(
            KnowledgeIngestionService ingestionService) {

        this.ingestionService = ingestionService;
    }

    @Override
    public void run(String... args) throws Exception {

        ingestionService.ingestKnowledge();
    }
}