package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tfg.model.Customer;

public interface CustomerDAO extends JpaRepository<Customer, String> {
}
