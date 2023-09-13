package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tfg.model.Company;
import org.tfg.model.Product;
import org.tfg.service.ProductService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("products")
@CrossOrigin("*")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/saveProduct")
    public void saveProduct(@RequestBody Map<String, Object> info){

        String name=info.get("name").toString();
        String details=info.get("details").toString();
        double price=(double)info.get("price");
        String companyId=info.get("companyId").toString();

        this.productService.save(name, details, price, companyId);
    }
}
