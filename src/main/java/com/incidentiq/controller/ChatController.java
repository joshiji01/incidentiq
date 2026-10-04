package com.incidentiq.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping
    public String chat(@RequestBody String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}