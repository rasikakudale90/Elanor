package com.elanor.ai.provider;

import com.elanor.ai.dto.AiChatRequest;
import com.elanor.ai.dto.AiChatResponse;
import com.elanor.ai.dto.AiConsultationRequest;
import com.elanor.ai.dto.AiConsultationResponse;
import com.elanor.ai.dto.CatalogContextDto;

import java.util.List;

public interface AiAssistantProvider {

    String getProviderName();

    AiChatResponse generateChatResponse(AiChatRequest request, List<CatalogContextDto> catalogContext);

    AiConsultationResponse generateConsultation(AiConsultationRequest request, List<CatalogContextDto> catalogContext);
}
