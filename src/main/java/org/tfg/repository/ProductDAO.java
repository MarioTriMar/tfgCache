package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tfg.model.Product;

public interface ProductDAO extends JpaRepository<Product, String> {
}
