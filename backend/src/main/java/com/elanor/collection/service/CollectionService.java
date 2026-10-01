package com.elanor.collection.service;

import com.elanor.collection.dto.*;
import com.elanor.collection.entity.Collection;
import com.elanor.collection.repository.CollectionRepository;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;

    public CollectionService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @Transactional(readOnly = true)
    public List<CollectionDto> getActiveCollections() {
        return collectionRepository.findByActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CollectionDto getBySlug(String slug) {
        Collection collection = collectionRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Collection not found with slug: " + slug));
        return mapToDto(collection);
    }

    @Transactional
    public CollectionDto createCollection(CreateCollectionRequest request) {
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
                ? generateSlug(request.getSlug())
                : generateSlug(request.getName());

        if (collectionRepository.existsBySlug(slug)) {
            throw new BusinessException(ErrorCode.CONFLICT, "Collection with slug '" + slug + "' already exists.");
        }

        Collection collection = new Collection();
        collection.setName(request.getName().trim());
        collection.setSlug(slug);
        collection.setDescription(request.getDescription());
        collection.setBannerUrl(request.getBannerUrl());
        collection.setActive(request.isActive());

        Collection saved = collectionRepository.save(collection);
        return mapToDto(saved);
    }

    @Transactional
    public CollectionDto updateCollection(UUID id, UpdateCollectionRequest request) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Collection not found."));

        collection.setName(request.getName().trim());
        collection.setDescription(request.getDescription());
        collection.setBannerUrl(request.getBannerUrl());
        collection.setActive(request.isActive());

        Collection saved = collectionRepository.save(collection);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteCollection(UUID id) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Collection not found."));
        collectionRepository.delete(collection);
    }

    private CollectionDto mapToDto(Collection collection) {
        return new CollectionDto(
                collection.getId(),
                collection.getName(),
                collection.getSlug(),
                collection.getDescription(),
                collection.getBannerUrl(),
                collection.isActive()
        );
    }

    private String generateSlug(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
