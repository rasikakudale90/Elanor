package com.elanor.ai.dto;

import java.util.List;
import java.util.UUID;

public class AiChatResponse {

    private String reply;
    private List<ProductRecommendationDto> suggestedProducts;
    private List<String> followUpQuestions;

    public AiChatResponse() {}

    public AiChatResponse(String reply, List<ProductRecommendationDto> suggestedProducts, List<String> followUpQuestions) {
        this.reply = reply;
        this.suggestedProducts = suggestedProducts;
        this.followUpQuestions = followUpQuestions;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<ProductRecommendationDto> getSuggestedProducts() {
        return suggestedProducts;
    }

    public void setSuggestedProducts(List<ProductRecommendationDto> suggestedProducts) {
        this.suggestedProducts = suggestedProducts;
    }

    public List<String> getFollowUpQuestions() {
        return followUpQuestions;
    }

    public void setFollowUpQuestions(List<String> followUpQuestions) {
        this.followUpQuestions = followUpQuestions;
    }
}
