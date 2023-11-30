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
import org.tfg.model.Order;
import org.tfg.model.Product;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.CustomerDAO;
import org.tfg.repository.ProductDAO;


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
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private CustomerDAO customerDAO;

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
    void changeCompanyState(){
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

        productController.changeStock("042cef3f-ba79-4d11-9cab-16258e59ac22","39b58bf7-e906-4a48-a950-e425191e3779");
        product=productController.getProductById("042cef3f-ba79-4d11-9cab-16258e59ac22");
        assertFalse(product.isStock());

        productController.changeStock("042cef3f-ba79-4d11-9cab-16258e59ac22","39b58bf7-e906-4a48-a950-e425191e3779");
    }
    @Test
    void wrongEmailCustomerRegister(){
        Map<String, Object> info=new HashMap<>();
        info.put("name","Pepe");
        info.put("email","12345");
        try{
            customerController.saveCustomer(info);
        }catch (ResponseStatusException e){
            assertEquals("406 NOT_ACCEPTABLE \"Incorrect email\"", e.getMessage());
        }
    }

    @Test
    void findCustomerWrongId(){
        try{
            customerController.getCustomerById("1");
        }catch (ResponseStatusException e){
            assertEquals("404 NOT_FOUND \"Customer doesn't exist\"", e.getMessage());
        }
    }
    @Test
    void wrongEmailCompanyRegister(){
        Map<String, Object> info=new HashMap<>();
        info.put("name","Restaurante");
        info.put("cif","12345");
        info.put("contactEmail","12345");
        try{
            companyController.saveCompany(info);
        }catch (ResponseStatusException e){
            assertEquals("406 NOT_ACCEPTABLE \"Incorrect email\"", e.getMessage());
        }
    }
    @Test
    void findCompanyWrongId(){
        try{
            companyController.getCompanyById("1");
        }catch (ResponseStatusException e){
            assertEquals("404 NOT_FOUND \"Company doesn't exist\"", e.getMessage());
        }
    }
    @Test
    void saveOrderWrongCustomer(){
        try{
            Map<String, Object> info=new HashMap<>();
            info.put("companyId", "06814f98-206b-4e69-a85a-2fcfa4c63996");
            info.put("customerId", "1");
            List<String> products=new ArrayList<>();
            products.add("9fd92fc2-a64d-425a-a5c1-e03f686e9954");
            products.add("dd7db431-fa41-44d9-93fe-91d5ace496d7");
            info.put("products",products);
        }catch (ResponseStatusException e){
            assertEquals("404 NOT_FOUND \"Customer doesn't exist\"", e.getMessage());
        }
    }
    @Test
    void saveOrderWrongCompany(){
        try{
            Map<String, Object> info=new HashMap<>();
            info.put("companyId", "1");
            info.put("customerId", "1");
            List<String> products=new ArrayList<>();
            products.add("9fd92fc2-a64d-425a-a5c1-e03f686e9954");
            products.add("dd7db431-fa41-44d9-93fe-91d5ace496d7");
            info.put("products",products);
        }catch (ResponseStatusException e){
            assertEquals("404 NOT_FOUND \"Company doesn't exist\"", e.getMessage());
        }
    }
    @Test
    void updateProduct(){
        Map<String,Object> info= new HashMap<>();
        info.put("id","042cef3f-ba79-4d11-9cab-16258e59ac22");
        info.put("name","Samsung s23");
        info.put("details","Móvil android de última generación");
        info.put("price", 1200.00);
        info.put("stock", true);
        info.put("company","39b58bf7-e906-4a48-a950-e425191e3779");
        productController.updateProduct(info);
        Product product=productController.getProductById("042cef3f-ba79-4d11-9cab-16258e59ac22");
        assertEquals(1200.00, product.getPrice());
    }
    @Test
    void findProductWrongId(){
        try{
            productController.getProductById("1");
        }catch (ResponseStatusException e){
            assertEquals("404 NOT_FOUND \"Product doesn't exist\"", e.getMessage());
        }
    }
    @Test
    void updateCustomer(){
        Customer customer = customerController.getCustomerById("f0592475-15c8-49b9-b7fa-ffa06ee59909");
        customer.setEmail("guille@gmail.com");
        customerController.update(customer);
        customer=customerController.getCustomerById("f0592475-15c8-49b9-b7fa-ffa06ee59909");
        assertEquals("guille@gmail.com", customer.getEmail());
    }
    @Test
    void updateCompany(){
        Company company=companyController.getCompanyById("06814f98-206b-4e69-a85a-2fcfa4c63996");
        company.setContactEmail("zaraContact@gmail.com");
        companyController.update(company);
        company=companyController.getCompanyById("06814f98-206b-4e69-a85a-2fcfa4c63996");
        assertEquals("zaraContact@gmail.com", company.getContactEmail());
    }
    @Test
    void changeCustomerState(){
        customer=customerController.getCustomerById("092ea791-305c-4576-8439-a44c78308974");
        assertTrue(customer.isEnabled());

        customerController.changeState("092ea791-305c-4576-8439-a44c78308974");
        customer=customerController.getCustomerById("092ea791-305c-4576-8439-a44c78308974");
        assertFalse(customer.isEnabled());

        customerController.changeState("092ea791-305c-4576-8439-a44c78308974");
    }
    @Test
    void getCustomers(){
        List<Customer> customers=customerController.getCustomers();
        assertEquals(customers.size(), 9);
    }
    @Test
    void getAllOrders(){
        List<Order> orders = orderController.getAll();
        assertEquals(134, orders.size());
    }
    @Test
    void getOrderByWrongId(){
        try{
            orderController.getOrderById("1");
        }catch (ResponseStatusException e){
            assertEquals("404 NOT_FOUND \"Order doesn't exist\"", e.getMessage());
        }
    }
    @Test
    void getOrdersOfCompany(){
        List<Order> orders=orderController.getOrdersOfCompany("39b58bf7-e906-4a48-a950-e425191e3779");
        assertEquals(18, orders.size());
    }
    @Test
    void getOrdersOfCustomer(){
        List<Order> orders=orderController.getOrdersOfCustomer("f0592475-15c8-49b9-b7fa-ffa06ee59909");
        assertEquals(14, orders.size());
    }
    @Test
    void saveProduct(){
        Map<String, Object> product=new HashMap<>();
        product.put("name","Atún");
        product.put("details", "90gr");
        product.put("price",4.50);
        product.put("companyId","5f5b217b-94b5-49e7-b2fc-018d18f2b483");
        List<Product> companyProducts=companyController.getCompanyProducts("5f5b217b-94b5-49e7-b2fc-018d18f2b483");
        assertEquals(7, companyProducts.size());
        Product producto=productController.saveProduct(product);
        companyProducts=companyController.getCompanyProducts("5f5b217b-94b5-49e7-b2fc-018d18f2b483");
        assertEquals(8, companyProducts.size());
        productDAO.delete(producto);
    }
    @Test
    void saveCompany(){
        Map<String,Object> info=new HashMap<>();
        info.put("name","Coviran");
        info.put("cif","dawdawd");
        info.put("contactEmail","coviran@email.com");
        Company company=companyController.saveCompany(info);
        Company companyFind=companyController.getCompanyById(company.getId());
        assertEquals(companyFind.getName(), "Coviran");
        companyDAO.delete(company);
    }
    @Test
    void saveCustomer(){
        Map<String, Object> info=new HashMap<>();
        info.put("name","Dani");
        info.put("email","dani@email.com");
        Customer customer=customerController.saveCustomer(info);
        Customer customerFind=customerController.getCustomerById(customer.getId());
        assertEquals(customerFind.getName(), "Dani");
        customerDAO.delete(customer);
    }
}