package org.tfg.model;

import jakarta.persistence.*;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Entity
@Table(
        name="customers",
        indexes = {
                @Index(columnList = "name", unique = true),
                @Index(columnList = "email", unique = true)
        }
)
public class Customer {
    @Id
    @Column(length=36)
    private String id;
    @Column(length=100) @NotEmpty
    private String name;
    @Column(length=140) @NotEmpty
    private String email;

    public Customer(){
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
