package com.fabiankressin.inventory.backend.dto;

public record CreateProductRequest(
        String name,
        int quantity
) {}
