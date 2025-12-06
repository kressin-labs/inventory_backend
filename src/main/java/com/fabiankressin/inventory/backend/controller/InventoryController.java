package com.fabiankressin.inventory.backend.controller;

import com.fabiankressin.inventory.backend.dto.CreateProductRequest;
import com.fabiankressin.inventory.backend.dto.QuantityUpdateRequest;
import com.fabiankressin.inventory.backend.model.Product;
import com.fabiankressin.inventory.backend.service.InventoryService;
import com.fabiankressin.inventory.backend.dto.ProductResponse;

import jakarta.annotation.security.PermitAll;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventory;

    public InventoryController(InventoryService inventory) {
        this.inventory = inventory;
    }

    @PermitAll
    @GetMapping
    public List<Product> listAll() {
        return inventory.listAll();
    }

    @PermitAll
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return inventory.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ProductResponse create(@RequestBody CreateProductRequest request) {
        Product savedProduct = inventory.create(request);
        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getQuantity(),
                savedProduct.getImageBase64()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inventory.delete(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/{id}/increase")
    public Product increase(@PathVariable Long id, @RequestBody QuantityUpdateRequest req) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isUser = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        // Users may only increase up to 5
        if (isUser && (req.amount() > 5 || req.amount() < 0)) {
            throw new IllegalArgumentException("Users can only increase by up to 5 at a time.");
        }

        return inventory.increase(id, req.amount());
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/{id}/decrease")
    public Product decrease(@PathVariable Long id, @RequestBody QuantityUpdateRequest req) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isUser = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        // Users may only increase up to 5
        if (isUser && (req.amount() > 5 || req.amount() < 0)) {
            throw new IllegalArgumentException("Users can only decrease by up to 5 at a time.");
        }

        return inventory.decrease(id, req.amount());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/set")
    public Product setQuantity(@PathVariable Long id, @RequestBody QuantityUpdateRequest req) {
        return inventory.setQuantity(id, req.amount());
    }
}
