package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tfg.model.Product;

import java.util.List;

@Repository
public interface ProductDAO extends JpaRepository<Product, String> {
    List<Product> findByCompanyId(String company);
}
