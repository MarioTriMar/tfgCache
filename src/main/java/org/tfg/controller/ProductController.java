package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tfg.model.Company;
import org.tfg.model.Product;
import org.tfg.service.ProductService;

import java.util.List;
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

    @PutMapping("/updateProduct")
    public void updateProduct(@RequestBody Map<String,Object> product){
        String id=product.get("id").toString();
        String name=product.get("name").toString();
        String details=product.get("details").toString();
        double price=(double)product.get("price");
        String companyId=product.get("company").toString();
        this.productService.updateProduct(id,name,details,price,companyId);
    }

    @GetMapping("/getProducts")
    public List<Product> getAllProducts(){
        return this.productService.getAll();
    }
}
