package org.tfg.model;

import javax.validation.constraints.NotEmpty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(
        name="companies",
        indexes = {
                @Index(columnList = "name", unique = true),
                @Index(columnList = "cif", unique = true)
        }
)
public class Company implements Serializable {
    @Id @Column(length=36)
    private String id;
    //id de la compañía, se genera la llamar al constructor

    @Column(length=100) @NotEmpty
    private String name;
    //nombre de la compañía

    @Column(length=100) @NotEmpty
    private String cif;
    //codigo de identificación fiscal

    @Column(length=140) @NotEmpty
    private String contactEmail;
    //email de contacto

    @NotEmpty
    private boolean enabled;
    //flag para el estado de la compañia

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
