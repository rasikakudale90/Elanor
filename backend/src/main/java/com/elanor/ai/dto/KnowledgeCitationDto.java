package com.elanor.ai.dto;

public class KnowledgeCitationDto {

    private String sourceId;
    private String title;
    private String urlOrReference;
    private String excerpt;

    public KnowledgeCitationDto() {}

    public KnowledgeCitationDto(String sourceId, String title, String urlOrReference, String excerpt) {
        this.sourceId = sourceId;
        this.title = title;
        this.urlOrReference = urlOrReference;
        this.excerpt = excerpt;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlOrReference() {
        return urlOrReference;
    }

    public void setUrlOrReference(String urlOrReference) {
        this.urlOrReference = urlOrReference;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }
}
