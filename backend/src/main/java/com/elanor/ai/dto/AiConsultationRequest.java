package com.elanor.ai.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class AiConsultationRequest {

    @NotBlank(message = "Skin phenotype is required")
    private String skinType; // e.g. "DRY", "OILY", "COMBINATION", "NORMAL", "SENSITIVE"

    private List<String> primaryConcerns; // e.g. ["HYDRATION", "FINE_LINES", "BARRIER_REPAIR", "DULLNESS", "PIGMENTATION"]

    private String climate; // e.g. "HUMID", "ARID", "URBAN_POLLUTED", "TEMPERATE"

    private String ritualDepth; // e.g. "MINIMALIST_3_STEP", "COMPLETE_CELLULAR_RITUAL"

    private String preferredTexture; // e.g. "LIGHTWEIGHT_SERUM", "RICH_BALM", "SILK_EMULSION"

    private Integer ageGroup; // e.g. 25, 35, 45, 55

    public AiConsultationRequest() {}

    public AiConsultationRequest(String skinType, List<String> primaryConcerns, String climate, String ritualDepth, String preferredTexture, Integer ageGroup) {
        this.skinType = skinType;
        this.primaryConcerns = primaryConcerns;
        this.climate = climate;
        this.ritualDepth = ritualDepth;
        this.preferredTexture = preferredTexture;
        this.ageGroup = ageGroup;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    public List<String> getPrimaryConcerns() {
        return primaryConcerns;
    }

    public void setPrimaryConcerns(List<String> primaryConcerns) {
        this.primaryConcerns = primaryConcerns;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public String getRitualDepth() {
        return ritualDepth;
    }

    public void setRitualDepth(String ritualDepth) {
        this.ritualDepth = ritualDepth;
    }

    public String getPreferredTexture() {
        return preferredTexture;
    }

    public void setPreferredTexture(String preferredTexture) {
        this.preferredTexture = preferredTexture;
    }

    public Integer getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(Integer ageGroup) {
        this.ageGroup = ageGroup;
    }
}
