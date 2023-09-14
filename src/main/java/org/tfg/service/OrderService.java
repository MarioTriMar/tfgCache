package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.*;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.CustomerDAO;
import org.tfg.repository.OrderDAO;
import org.tfg.repository.ProductDAO;

import java.sql.Timestamp;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private ProductDAO productDAO;

    public void saveOrder(String companyId, String customerId, List<String> products) {
        String id= UUID.randomUUID().toString();
        Timestamp creationTime=new Timestamp(System.currentTimeMillis());
        Company company=existCompany(companyId);
        Customer customer=existCustomer(customerId);
        List<Product> productList=new ArrayList<>();
        for(int i=0;i<products.size();i++){
            Product product=existProduct(products.get(i));
            productList.add(product);
        }
        makeOrder(id, creationTime, company, customer, productList);
    }
    private void makeOrder(String id, Timestamp creationTime, Company company, Customer customer,
                          List<Product> productList){
        Order order=new Order();
        order.setId(id);
        order.setCreationTime(creationTime);
        order.setCompany(company);
        order.setCustomer(customer);
        order.setProduct(productList);
        for(int i=0;i<order.getProduct().size();i++){
            order.setPrice(order.getPrice()+order.getProduct().get(i).getPrice());
        }
        this.orderDAO.save(order);
    }
    public List<Order> getAll() {
        return this.orderDAO.findAll();
    }

    public Order findOrderById(String id) {
        Optional<Order> optOrder=this.orderDAO.findById(id);
        if(optOrder.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order doesn't exist");
        }
        return optOrder.get();
    }
    public List<Order> findByCompanyId(String companyId) {
        Company company=existCompany(companyId);
        return this.orderDAO.findByCompany(company);
    }

    public List<Order> findByCustomerId(String customerId) {
        Customer customer=existCustomer(customerId);
        return this.orderDAO.findByCustomer(customer);
    }

    private Company existCompany(String companyId){
        Optional<Company> optCompany=this.companyDAO.findById(companyId);
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        return optCompany.get();
    }
    private Customer existCustomer(String customerId){
        Optional<Customer> optCustomer=this.customerDAO.findById(customerId);
        if(optCustomer.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist");
        }
        return optCustomer.get();
    }
    private Product existProduct(String productId){
        Optional<Product> optionalProduct=this.productDAO.findById(productId);
        if(optionalProduct.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product doesn't exist");
        }
        return optionalProduct.get();
    }

}
