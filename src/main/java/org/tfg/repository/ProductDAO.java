package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tfg.model.Product;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product, String> {
    List<Product> findByCompanyId(String company);
}
