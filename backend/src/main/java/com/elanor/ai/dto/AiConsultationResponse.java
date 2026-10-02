package com.elanor.ai.dto;

import java.math.BigDecimal;
import java.util.List;

public class AiConsultationResponse {

    private String prescriptionTitle;
    private String dermalPhenotype;
    private VitalityScoreDto vitalityScores;
    private List<RitualStepDto> morningRitual;
    private List<RitualStepDto> eveningRitual;
    private BigDecimal ritualSubtotal;
    private BigDecimal bundledDiscountPercentage; // e.g. 15.00%
    private BigDecimal bundledPrice;
    private String professionalNote;

    public AiConsultationResponse() {}

    public AiConsultationResponse(String prescriptionTitle, String dermalPhenotype, VitalityScoreDto vitalityScores, List<RitualStepDto> morningRitual, List<RitualStepDto> eveningRitual, BigDecimal ritualSubtotal, BigDecimal bundledDiscountPercentage, BigDecimal bundledPrice, String professionalNote) {
        this.prescriptionTitle = prescriptionTitle;
        this.dermalPhenotype = dermalPhenotype;
        this.vitalityScores = vitalityScores;
        this.morningRitual = morningRitual;
        this.eveningRitual = eveningRitual;
        this.ritualSubtotal = ritualSubtotal;
        this.bundledDiscountPercentage = bundledDiscountPercentage;
        this.bundledPrice = bundledPrice;
        this.professionalNote = professionalNote;
    }

    public String getPrescriptionTitle() {
        return prescriptionTitle;
    }

    public void setPrescriptionTitle(String prescriptionTitle) {
        this.prescriptionTitle = prescriptionTitle;
    }

    public String getDermalPhenotype() {
        return dermalPhenotype;
    }

    public void setDermalPhenotype(String dermalPhenotype) {
        this.dermalPhenotype = dermalPhenotype;
    }

    public VitalityScoreDto getVitalityScores() {
        return vitalityScores;
    }

    public void setVitalityScores(VitalityScoreDto vitalityScores) {
        this.vitalityScores = vitalityScores;
    }

    public List<RitualStepDto> getMorningRitual() {
        return morningRitual;
    }

    public void setMorningRitual(List<RitualStepDto> morningRitual) {
        this.morningRitual = morningRitual;
    }

    public List<RitualStepDto> getEveningRitual() {
        return eveningRitual;
    }

    public void setEveningRitual(List<RitualStepDto> eveningRitual) {
        this.eveningRitual = eveningRitual;
    }

    public BigDecimal getRitualSubtotal() {
        return ritualSubtotal;
    }

    public void setRitualSubtotal(BigDecimal ritualSubtotal) {
        this.ritualSubtotal = ritualSubtotal;
    }

    public BigDecimal getBundledDiscountPercentage() {
        return bundledDiscountPercentage;
    }

    public void setBundledDiscountPercentage(BigDecimal bundledDiscountPercentage) {
        this.bundledDiscountPercentage = bundledDiscountPercentage;
    }

    public BigDecimal getBundledPrice() {
        return bundledPrice;
    }

    public void setBundledPrice(BigDecimal bundledPrice) {
        this.bundledPrice = bundledPrice;
    }

    public String getProfessionalNote() {
        return professionalNote;
    }

    public void setProfessionalNote(String professionalNote) {
        this.professionalNote = professionalNote;
    }
}
