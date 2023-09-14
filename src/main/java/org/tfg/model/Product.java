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

    @Column(length=100) @NotEmpty
    private String name;

    @NotEmpty
    private String details;
    @NotEmpty
    private double price;

    @ManyToOne
    private Company company;

    @ManyToMany(mappedBy = "product")
    private List<Order> orders;

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

}
