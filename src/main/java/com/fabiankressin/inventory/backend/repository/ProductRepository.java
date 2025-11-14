package com.fabiankressin.inventory.backend.repository;

import com.fabiankressin.inventory.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameIgnoreCase(String name);
}
