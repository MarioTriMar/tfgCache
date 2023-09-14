package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
import org.tfg.model.Product;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.ProductDAO;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CompanyDAO companyDAO;

    public void save(String name, String details, double price, String companyId) {
        Company company=existCompany(companyId);
        Product product=new Product();
        product.setName(name);
        product.setDetails(details);
        product.setPrice(price);
        product.setCompany(company);
        this.productDAO.save(product);
    }

    public void updateProduct(String id, String name, String details, double price, String companyId) {
        Company company=existCompany(companyId);
        Product product=existProduct(id);
        product.setCompany(company);
        product.setDetails(details);
        product.setPrice(price);
        product.setName(name);
        this.productDAO.save(product);
    }

    public List<Product> getAll() {
        return this.productDAO.findAll();
    }

    private Company existCompany(String companyId){
        Optional<Company> optCompany=this.companyDAO.findById(companyId);
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        return optCompany.get();
    }

    private Product existProduct(String productId){
        Optional<Product> optProduct=this.productDAO.findById(productId);
        if(optProduct.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product doesn't exist");
        }
        return optProduct.get();
    }
}
