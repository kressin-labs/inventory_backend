package com.fabiankressin.inventory.backend.dto;

public record ProductResponse(Long id, String name, int quantity, String imageBase64) {
}
