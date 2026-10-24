package com.elanor.ai.service;

import com.elanor.ai.dto.KnowledgeCitationDto;
import com.elanor.ai.dto.RetrievedChunkDto;
import com.elanor.ai.entity.KnowledgeChunk;
import com.elanor.ai.repository.KnowledgeChunkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class KnowledgeRetrievalService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeRetrievalService.class);

    private final KnowledgeChunkRepository knowledgeChunkRepository;

    public KnowledgeRetrievalService(KnowledgeChunkRepository knowledgeChunkRepository) {
        this.knowledgeChunkRepository = knowledgeChunkRepository;
    }

    @Transactional(readOnly = true)
    public List<RetrievedChunkDto> retrieveRelevant(String query, int topK, double minRelevance) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<KnowledgeChunk> allChunks = knowledgeChunkRepository.findAll();
        if (allChunks.isEmpty()) {
            log.warn("Knowledge base is currently empty during retrieval.");
            return Collections.emptyList();
        }

        Set<String> queryTokens = tokenize(query);
        List<ScoredChunk> scoredList = new ArrayList<>();

        for (KnowledgeChunk chunk : allChunks) {
            double score = computeRelevanceScore(query, queryTokens, chunk);
            if (score >= minRelevance) {
                scoredList.add(new ScoredChunk(chunk, score));
            }
        }

        scoredList.sort((a, b) -> Double.compare(b.score, a.score));

        return scoredList.stream()
                .limit(topK)
                .map(sc -> new RetrievedChunkDto(
                        sc.chunk.getId(),
                        sc.chunk.getSourceType(),
                        sc.chunk.getSourceId(),
                        sc.chunk.getTitle(),
                        sc.chunk.getContent(),
                        sc.score,
                        sc.chunk.getMetadataJson()
                ))
                .toList();
    }

    public KnowledgeCitationDto toCitation(RetrievedChunkDto chunk) {
        String urlOrRef = "/";
        if ("PRODUCT".equalsIgnoreCase(chunk.getSourceType())) {
            urlOrRef = "/product/" + chunk.getSourceId();
        } else if ("POLICY".equalsIgnoreCase(chunk.getSourceType())) {
            urlOrRef = "shipping-policy".equals(chunk.getSourceId()) ? "/track" : "/policies/returns";
        }

        String excerpt = chunk.getContent().length() > 140
                ? chunk.getContent().substring(0, 140) + "..."
                : chunk.getContent();

        return new KnowledgeCitationDto(
                chunk.getSourceType() + ":" + chunk.getSourceId(),
                chunk.getTitle(),
                urlOrRef,
                excerpt.replace("\n", " ")
        );
    }

    private double computeRelevanceScore(String rawQuery, Set<String> queryTokens, KnowledgeChunk chunk) {
        String lowerTitle = chunk.getTitle().toLowerCase(Locale.ROOT);
        String lowerContent = chunk.getContent().toLowerCase(Locale.ROOT);
        String lowerRawQuery = rawQuery.toLowerCase(Locale.ROOT);

        double score = 0.0;

        // Exact phrase in title is a massive match
        if (lowerTitle.contains(lowerRawQuery)) {
            score += 0.60;
        }

        // Exact phrase in content
        if (lowerContent.contains(lowerRawQuery)) {
            score += 0.35;
        }

        // Token overlap in title and content
        int titleMatches = 0;
        int contentMatches = 0;

        for (String token : queryTokens) {
            if (lowerTitle.contains(token)) {
                titleMatches++;
            }
            if (lowerContent.contains(token)) {
                contentMatches++;
            }
        }

        if (!queryTokens.isEmpty()) {
            double titleRatio = (double) titleMatches / queryTokens.size();
            double contentRatio = (double) contentMatches / queryTokens.size();
            score += (titleRatio * 0.40) + (contentRatio * 0.35);
        }

        return Math.min(1.0, score);
    }

    private Set<String> tokenize(String text) {
        Set<String> tokens = new HashSet<>();
        String[] words = text.toLowerCase(Locale.ROOT).split("[^a-zA-Z0-9]+");
        Set<String> stopWords = Set.of("the", "a", "an", "is", "in", "it", "of", "to", "and", "for", "with", "on", "at", "by", "what", "how", "do", "you", "have", "can", "i");

        for (String word : words) {
            if (word.length() > 2 && !stopWords.contains(word)) {
                tokens.add(word);
            }
        }
        return tokens;
    }

    private record ScoredChunk(KnowledgeChunk chunk, double score) {}
}
