package org.tfg.model;

import jakarta.persistence.*;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.sql.Timestamp;

@Entity
@Table(
        name="orders"
)
public class Order {
    @Id
    @Column(length=36)
    private String id;
    //id del pedido

    @ManyToOne
    private Company company;
    /*
    Relación, una compañia puede tener muchos
    pedidos y un pedido pertenece a una compañía.
     */

    @ManyToOne
    private Customer customer;
    /*
    Relación, un cliente puede tener muchos
    pedidos y un pedido pertenece a un cliente.
     */

    @ManyToMany
    @JoinTable(
            name="orders_product",
            joinColumns = @JoinColumn(name="order_id"),
            inverseJoinColumns = @JoinColumn(name="product_id")
    )
    private List<Product> product;
    /*
    Relación, un pedido puede tener muchos
    productos y un producto puede pertenecer a muchos pedidos.
     */

    @NotEmpty
    private double price;
    //precio total del pedido (suma de precio de productos)

    @NotEmpty
    private Timestamp creationTime;
    //fecha de creación del pedido
    public Order(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }



    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }



    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }



}
