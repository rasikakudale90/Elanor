package com.elanor.search.dto;

import java.math.BigDecimal;

public class SearchFilterRequest {
    private String query;
    private String categorySlug;
    private String collectionSlug;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String badge;
    private String sortBy; // price_asc, price_desc, newest, name_asc, name_desc
    private int page = 0;
    private int size = 20;

    public SearchFilterRequest() {}

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public String getCollectionSlug() {
        return collectionSlug;
    }

    public void setCollectionSlug(String collectionSlug) {
        this.collectionSlug = collectionSlug;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
