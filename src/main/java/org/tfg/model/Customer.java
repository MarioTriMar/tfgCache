package org.tfg.model;

import jakarta.persistence.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(
        name="customers",
        indexes = {
                @Index(columnList = "name", unique = true),
                @Index(columnList = "email", unique = true)
        }
)
public class Customer implements Serializable {
    @Id
    @Column(length=36)
    private String id;
    //id del cliente, se genera al llamar al constructor

    @Column(length=100) @NotEmpty
    private String name;
    //nombre del cliente

    @Column(length=140) @NotEmpty
    private String email;
    //email del cliente

    @NotEmpty
    private boolean enabled;
    //flag para el estado del cliente

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
