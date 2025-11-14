package com.fabiankressin.inventory.backend.service;

import com.fabiankressin.inventory.backend.dto.CreateProductRequest;
import com.fabiankressin.inventory.backend.model.Product;
import com.fabiankressin.inventory.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final ProductRepository products;

    public InventoryService(ProductRepository products) {
        this.products = products;
    }

    public List<Product> listAll() {
        return products.findAll();
    }

    public Product getById(Long id) {
        return products.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product create(CreateProductRequest request) {
        if (products.findByNameIgnoreCase(request.name()).isPresent()) {
            throw new RuntimeException("Product with this name already exists");
        }
        Product p = Product.builder()
                .name(request.name())
                .quantity(request.quantity())
                .build();
        return products.save(p);
    }

    public void delete(Long id) {
        if (!products.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        products.deleteById(id);
    }

    public Product increase(Long id, int amount) {
        Product p = getById(id);
        p.setQuantity(p.getQuantity() + amount);
        return products.save(p);
    }

    public Product decrease(Long id, int amount) {
        Product p = getById(id);
        int newAmount = p.getQuantity() - amount;
        if (newAmount < 0) newAmount = 0; // Or throw exception
        p.setQuantity(newAmount);
        return products.save(p);
    }

    public Product setQuantity(Long id, int amount) {
        Product p = getById(id);
        p.setQuantity(amount);
        return products.save(p);
    }
}
