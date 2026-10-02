package com.elanor.ai.controller;

import com.elanor.ai.dto.AiChatRequest;
import com.elanor.ai.dto.AiChatResponse;
import com.elanor.ai.dto.AiConsultationRequest;
import com.elanor.ai.dto.AiConsultationResponse;
import com.elanor.ai.service.AiAssistantService;
import com.elanor.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Concierge & Regimen Engine", description = "Haute AI Skin Concierge, diagnostic consultation, and formulation advisor")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Haute Botanical Beauty Concierge Chat", description = "Interactive AI beauty concierge answering formulation and product queries")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(@Valid @RequestBody AiChatRequest request) {
        AiChatResponse response = aiAssistantService.chat(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/consult")
    @Operation(summary = "Haute Skin Diagnostic Consultation", description = "4-Step diagnostic evaluator generating tailored morning/evening rituals and vitality scores")
    public ResponseEntity<ApiResponse<AiConsultationResponse>> consult(@Valid @RequestBody AiConsultationRequest request) {
        AiConsultationResponse response = aiAssistantService.consult(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
