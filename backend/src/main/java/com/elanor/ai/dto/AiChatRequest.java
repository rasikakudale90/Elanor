package com.elanor.ai.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public class AiChatRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

    private List<ChatMessageDto> history;

    private UUID focusedProductId;

    public AiChatRequest() {}

    public AiChatRequest(String message, List<ChatMessageDto> history, UUID focusedProductId) {
        this.message = message;
        this.history = history;
        this.focusedProductId = focusedProductId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ChatMessageDto> getHistory() {
        return history;
    }

    public void setHistory(List<ChatMessageDto> history) {
        this.history = history;
    }

    public UUID getFocusedProductId() {
        return focusedProductId;
    }

    public void setFocusedProductId(UUID focusedProductId) {
        this.focusedProductId = focusedProductId;
    }
}
