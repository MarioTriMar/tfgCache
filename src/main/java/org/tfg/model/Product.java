package org.tfg.model;

import jakarta.persistence.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name="products"
)
public class Product {
    @Id
    @Column(length=36)
    private String id;
    //id del producto, se genera al llamar al constructor

    @Column(length=100) @NotEmpty
    private String name;
    //nombre del producto

    @NotEmpty
    private String details;
    //detalles del producto (material, talla, etc)

    @NotEmpty
    private double price;
    //precio del producto

    @NotEmpty
    private boolean stock;
    //flag para el estado del producto

    @ManyToOne
    private Company company;
    /*
    Relación, una compañia puede tener muchos
    productos y un producto pertenece a una compañía.
     */

    @ManyToMany(mappedBy = "product")
    private List<Order> orders;
    /*
    Relación, un pedido puede tener muchos
    productos y un producto puede pertenecer a muchos pedidos.
     */

    public Product(){
        this.id= UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }

}
