package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tfg.model.Order;

public interface OrderDAO extends JpaRepository<Order, String> {
}
