package com.fabiankressin.inventory.backend.controller;

import com.fabiankressin.inventory.backend.dto.CreateProductRequest;
import com.fabiankressin.inventory.backend.dto.QuantityUpdateRequest;
import com.fabiankressin.inventory.backend.model.Product;
import com.fabiankressin.inventory.backend.service.InventoryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventory;

    public InventoryController(InventoryService inventory) {
        this.inventory = inventory;
    }

    @GetMapping
    public List<Product> listAll() {
        return inventory.listAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return inventory.getById(id);
    }

    @PostMapping
    public Product create(@RequestBody CreateProductRequest request) {
        return inventory.create(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inventory.delete(id);
    }

    @PostMapping("/{id}/increase")
    public Product increase(@PathVariable Long id, @RequestBody QuantityUpdateRequest req) {
        return inventory.increase(id, req.amount());
    }

    @PostMapping("/{id}/decrease")
    public Product decrease(@PathVariable Long id, @RequestBody QuantityUpdateRequest req) {
        return inventory.decrease(id, req.amount());
    }

    @PostMapping("/{id}/set")
    public Product setQuantity(@PathVariable Long id, @RequestBody QuantityUpdateRequest req) {
        return inventory.setQuantity(id, req.amount());
    }
}
