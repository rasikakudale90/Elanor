package com.elanor.ai.dto;

public class VitalityScoreDto {

    private int hydration; // 0 - 100
    private int barrierIntegrity; // 0 - 100
    private int cellularRadiance; // 0 - 100
    private int dermalReactivity; // 0 - 100 (higher = calmer / less reactive)
    private String summaryDiagnostic;

    public VitalityScoreDto() {}

    public VitalityScoreDto(int hydration, int barrierIntegrity, int cellularRadiance, int dermalReactivity, String summaryDiagnostic) {
        this.hydration = hydration;
        this.barrierIntegrity = barrierIntegrity;
        this.cellularRadiance = cellularRadiance;
        this.dermalReactivity = dermalReactivity;
        this.summaryDiagnostic = summaryDiagnostic;
    }

    public int getHydration() {
        return hydration;
    }

    public void setHydration(int hydration) {
        this.hydration = hydration;
    }

    public int getBarrierIntegrity() {
        return barrierIntegrity;
    }

    public void setBarrierIntegrity(int barrierIntegrity) {
        this.barrierIntegrity = barrierIntegrity;
    }

    public int getCellularRadiance() {
        return cellularRadiance;
    }

    public void setCellularRadiance(int cellularRadiance) {
        this.cellularRadiance = cellularRadiance;
    }

    public int getDermalReactivity() {
        return dermalReactivity;
    }

    public void setDermalReactivity(int dermalReactivity) {
        this.dermalReactivity = dermalReactivity;
    }

    public String getSummaryDiagnostic() {
        return summaryDiagnostic;
    }

    public void setSummaryDiagnostic(String summaryDiagnostic) {
        this.summaryDiagnostic = summaryDiagnostic;
    }
}
