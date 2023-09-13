package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
import org.tfg.model.Product;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.ProductDAO;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CompanyDAO companyDAO;

    public void save(String name, String details, double price, String companyId) {
        Optional<Company> optCompany=this.companyDAO.findById(companyId);
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        Product product=new Product();
        product.setName(name);
        product.setDetails(details);
        product.setPrice(price);
        product.setCompany(optCompany.get());
        this.productDAO.save(product);
    }
}
