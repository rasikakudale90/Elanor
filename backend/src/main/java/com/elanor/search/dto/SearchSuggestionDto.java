package com.elanor.search.dto;

import com.elanor.catalog.dto.ProductSummaryDto;
import com.elanor.category.dto.CategoryDto;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionDto {
    private String query;
    private List<String> suggestions = new ArrayList<>();
    private List<ProductSummaryDto> products = new ArrayList<>();
    private List<CategoryDto> categories = new ArrayList<>();

    public SearchSuggestionDto() {}

    public SearchSuggestionDto(String query, List<String> suggestions, List<ProductSummaryDto> products, List<CategoryDto> categories) {
        this.query = query;
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
        this.products = products != null ? products : new ArrayList<>();
        this.categories = categories != null ? categories : new ArrayList<>();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<ProductSummaryDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductSummaryDto> products) {
        this.products = products;
    }

    public List<CategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }
}
