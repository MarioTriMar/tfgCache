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
public class OrderSevice {

    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private ProductDAO productDAO;

    public void saveOrder(String companyId, String customerId, Map<String, Product> products) {
        String id= UUID.randomUUID().toString();
        Timestamp creationTime=new Timestamp(System.currentTimeMillis());
        Optional<Company> optCompany = this.companyDAO.findById(companyId);
        Optional<Customer> optCustomer=this.customerDAO.findById(customerId);
        if(optCustomer.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist");
        }
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        Set<String> keys=products.keySet();
        List<String> keyList=new ArrayList<>(keys);
        List<Product> productList=new ArrayList<>();
        for(int i=0;i<keyList.size();i++){
            Optional<Product> optProduct=this.productDAO.findById(keyList.get(i));
            if(optProduct.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product doesn't exist");
            }
            productList.add(optProduct.get());
        }
        Order order=new Order();
        order.setId(id);
        order.setCreationTime(creationTime);
        order.setCompany(optCompany.get());
        order.setCustomer(optCustomer.get());
        order.setProduct(productList);
        for(int i=0;i<order.getProduct().size();i++){
            order.setPrice(order.getPrice()+order.getProduct().get(i).getPrice());
        }
        this.orderDAO.save(order);
        /*
        for(int i=0;i<order.getProduct().size();i++){
            this.orderDAO.updateOrdersProduct(order.getId(), order.getProduct().get(i).getId(),
                    Integer.parseInt(products.get(order.getProduct().get(i).getId()).toString()));
        }
         */
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
        Optional<Company> optCompany = this.companyDAO.findById(companyId);
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        return this.orderDAO.findByCompany(optCompany.get());
    }

    public List<Order> findByCustomerId(String customerId) {
        Optional<Customer> optCustomer = this.customerDAO.findById(customerId);
        if(optCustomer.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist");
        }
        return this.orderDAO.findByCustomer(optCustomer.get());
    }


}
