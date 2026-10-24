package com.elanor.ai.repository;

import com.elanor.ai.entity.KnowledgeChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeChunkRepository extends JpaRepository<KnowledgeChunk, UUID> {

    Optional<KnowledgeChunk> findBySourceTypeAndSourceId(String sourceType, String sourceId);

    Optional<KnowledgeChunk> findByContentHash(String contentHash);

    List<KnowledgeChunk> findAllBySourceType(String sourceType);

    void deleteBySourceTypeAndSourceId(String sourceType, String sourceId);

    @Query("SELECT k FROM KnowledgeChunk k WHERE LOWER(k.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(k.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<KnowledgeChunk> searchByKeyword(@Param("keyword") String keyword);
}
