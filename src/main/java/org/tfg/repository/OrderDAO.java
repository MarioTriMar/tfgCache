package org.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tfg.model.Company;
import org.tfg.model.Customer;
import org.tfg.model.Order;

import java.util.List;


public interface OrderDAO extends JpaRepository<Order, String> {
    List<Order> findByCompany(Company company);

    List<Order> findByCustomer(Customer customer);

    /*
    @Query("UPDATE orders_product SET quantity =:quantity WHERE order_id=:order AND product_id=:product")
    void updateOrdersProduct(@Param("order") String orderId, @Param("product") String
                             productId, @Param("quantity") int quantity);

     */
}
