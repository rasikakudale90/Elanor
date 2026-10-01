package com.elanor.common.idempotency.service;

import com.elanor.common.idempotency.entity.IdempotencyRecord;
import com.elanor.common.idempotency.repository.IdempotencyRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyRecordRepository idempotencyRecordRepository, ObjectMapper objectMapper) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public <T> Optional<T> getCachedResponse(String idempotencyKey, Class<T> responseType) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }

        return idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey.trim())
                .flatMap(record -> {
                    try {
                        if (record.getResponseBody() != null) {
                            T response = objectMapper.readValue(record.getResponseBody(), responseType);
                            log.info("Idempotent cache hit for key [{}] on resource [{}]", idempotencyKey, record.getResourceType());
                            return Optional.of(response);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to deserialize cached idempotent response for key [{}]: {}", idempotencyKey, e.getMessage());
                    }
                    return Optional.empty();
                });
    }

    @Transactional
    public void saveRecord(String idempotencyKey, String resourceType, String resourceId, Object responseObject, int statusCode) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(responseObject);
            IdempotencyRecord record = new IdempotencyRecord(idempotencyKey.trim(), resourceType, resourceId, json, statusCode);
            idempotencyRecordRepository.save(record);
            log.info("Saved idempotency record for key [{}] resource [{}] id [{}]", idempotencyKey, resourceType, resourceId);
        } catch (Exception e) {
            log.error("Failed to serialize and save idempotency record for key [{}]: {}", idempotencyKey, e.getMessage());
        }
    }
}
