package org.tfg.model;

import javax.validation.constraints.NotEmpty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(
        name="companies",
        indexes = {
                @Index(columnList = "name", unique = true),
                @Index(columnList = "cif", unique = true)
        }
)
public class Company {
    @Id @Column(length=36)
    private String id;
    @Column(length=100) @NotEmpty
    private String name;
    @Column(length=100) @NotEmpty
    private String cif;
    @Column(length=140) @NotEmpty
    private String contactEmail;

    public Company(){
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

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
