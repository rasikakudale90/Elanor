package com.elanor.ai.dto;

import java.util.ArrayList;
import java.util.List;

public class AiChatResponse {

    private String reply;
    private List<ProductRecommendationDto> suggestedProducts = new ArrayList<>();
    private List<String> followUpQuestions = new ArrayList<>();
    private List<KnowledgeCitationDto> citations = new ArrayList<>();
    private boolean needsHumanSupport;
    private String intent;

    public AiChatResponse() {}

    public AiChatResponse(String reply, List<ProductRecommendationDto> suggestedProducts, List<String> followUpQuestions) {
        this.reply = reply;
        this.suggestedProducts = suggestedProducts != null ? suggestedProducts : new ArrayList<>();
        this.followUpQuestions = followUpQuestions != null ? followUpQuestions : new ArrayList<>();
    }

    public AiChatResponse(String reply, List<ProductRecommendationDto> suggestedProducts, List<String> followUpQuestions,
                          List<KnowledgeCitationDto> citations, boolean needsHumanSupport, String intent) {
        this.reply = reply;
        this.suggestedProducts = suggestedProducts != null ? suggestedProducts : new ArrayList<>();
        this.followUpQuestions = followUpQuestions != null ? followUpQuestions : new ArrayList<>();
        this.citations = citations != null ? citations : new ArrayList<>();
        this.needsHumanSupport = needsHumanSupport;
        this.intent = intent;
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

    public List<KnowledgeCitationDto> getCitations() {
        return citations;
    }

    public void setCitations(List<KnowledgeCitationDto> citations) {
        this.citations = citations;
    }

    public boolean isNeedsHumanSupport() {
        return needsHumanSupport;
    }

    public void setNeedsHumanSupport(boolean needsHumanSupport) {
        this.needsHumanSupport = needsHumanSupport;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }
}
