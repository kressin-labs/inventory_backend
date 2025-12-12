package com.fabiankressin.inventory.backend.dto;
import com.fabiankressin.inventory.backend.model.Product;

public record ProductResponse(Long id, String name, int quantity, String imageBase64, String info) {
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getImageBase64(),
                product.getInfo()
        );
    }
}
