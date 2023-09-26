package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tfg.model.Customer;

@Repository
public interface CustomerDAO extends JpaRepository<Customer, String> {
}
