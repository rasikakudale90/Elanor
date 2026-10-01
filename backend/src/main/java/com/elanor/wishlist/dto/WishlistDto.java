package com.elanor.wishlist.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WishlistDto {

    private UUID id;
    private int totalItems;
    private List<WishlistItemDto> items = new ArrayList<>();

    public WishlistDto() {}

    public WishlistDto(UUID id, List<WishlistItemDto> items) {
        this.id = id;
        this.items = items != null ? items : new ArrayList<>();
        this.totalItems = this.items.size();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<WishlistItemDto> getItems() {
        return items;
    }

    public void setItems(List<WishlistItemDto> items) {
        this.items = items;
        this.totalItems = items != null ? items.size() : 0;
    }
}
