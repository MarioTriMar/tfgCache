package org.tfg;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.controller.CompanyController;
import org.tfg.controller.CustomerController;
import org.tfg.controller.OrderController;
import org.tfg.controller.ProductController;
import org.tfg.model.Company;
import org.tfg.model.Customer;
import org.tfg.model.Product;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;


@SpringBootTest
public class TfgCacheApplicationTest {

    @Autowired
    private CompanyController companyController;
    @Autowired
    private CustomerController customerController;
    @Autowired
    private OrderController orderController;
    @Autowired
    private ProductController productController;
    private Company company;
    private Customer customer;

    private Product product;

    @Test
    void findCompanyProductsError() {
        try{
            List<Product> productList=companyController.getCompanyProducts("1");
        }catch(ResponseStatusException e){
            assertEquals("404 NOT_FOUND \"Company doesn't exist\"", e.getMessage());
        }
    }

    @Test
    void changeState(){
        company=companyController.getCompanyById("f1a46207-edfe-4b32-88ff-555b129d8cfe");
        assertTrue(company.isEnabled());

        companyController.changeState("f1a46207-edfe-4b32-88ff-555b129d8cfe");
        company=companyController.getCompanyById("f1a46207-edfe-4b32-88ff-555b129d8cfe");
        assertFalse(company.isEnabled());

        companyController.changeState("f1a46207-edfe-4b32-88ff-555b129d8cfe");
    }

    @Test
    void indexAllCompanies(){
        List<Company> companies=this.companyController.getCompanies();
        assertEquals(companies.size(),8);
    }

    @Test
    void getCustomerById(){
        customer = this.customerController.getCustomerById("5226558e-b4cf-41a3-be8f-2355c6f6d4c4");
        assertEquals(customer.getName(), "Pepe");
    }

    @Test
    void productsBelongToCompany(){
        Map<String, Object> info=new HashMap<>();
        info.put("companyId", "06814f98-206b-4e69-a85a-2fcfa4c63996");
        info.put("customerId", "5226558e-b4cf-41a3-be8f-2355c6f6d4c4");
        List<String> products=new ArrayList<>();
        products.add("9fd92fc2-a64d-425a-a5c1-e03f686e9954");
        products.add("dd7db431-fa41-44d9-93fe-91d5ace496d7");
        info.put("products",products);
        try{
            orderController.saveOrder(info);
        }catch (ResponseStatusException e){
            assertEquals("406 NOT_ACCEPTABLE \"Products doesn't belong to the company\"", e.getMessage());
        }

    }

    @Test
    void totalMoney(){
        double money=orderController.getTotalMoney("af78d9bd-91f8-4974-8645-a77c9f1c142a");
        assertEquals(278.0,money);
    }

    @Test
    void getProductById(){
        product=productController.getProductById("9fd92fc2-a64d-425a-a5c1-e03f686e9954");
        assertEquals(product.getName(), "Americana azul");
        assertEquals(product.getDetails(), "Talla M");
    }

    @Test
    void getAllProducts(){
        List<Product> products = productController.getAllProducts();
        assertEquals(56, products.size());
    }

    @Test
    void changeProductStock(){
        product=productController.getProductById("042cef3f-ba79-4d11-9cab-16258e59ac22");
        assertTrue(product.isStock());

        productController.changeStock("042cef3f-ba79-4d11-9cab-16258e59ac22");
        product=productController.getProductById("042cef3f-ba79-4d11-9cab-16258e59ac22");
        assertFalse(product.isStock());

        productController.changeStock("042cef3f-ba79-4d11-9cab-16258e59ac22");
    }
}
