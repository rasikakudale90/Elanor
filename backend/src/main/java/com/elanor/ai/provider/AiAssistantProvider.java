package com.elanor.ai.provider;

import com.elanor.ai.dto.*;

import java.util.Collections;
import java.util.List;

public interface AiAssistantProvider {

    String getProviderName();

    default AiChatResponse generateChatResponse(AiChatRequest request, List<CatalogContextDto> catalogContext) {
        return generateChatResponse(request, catalogContext, Collections.emptyList());
    }

    AiChatResponse generateChatResponse(AiChatRequest request, List<CatalogContextDto> catalogContext, List<RetrievedChunkDto> retrievedEvidence);

    AiConsultationResponse generateConsultation(AiConsultationRequest request, List<CatalogContextDto> catalogContext);
}
