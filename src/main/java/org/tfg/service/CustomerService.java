package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tfg.model.Customer;
import org.tfg.repository.CustomerDAO;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerDAO customerDAO;
    public void save(String name, String email){
        Customer customer=new Customer();
        customer.setName(name);
        customer.setEmail(email);
        this.customerDAO.save(customer);
    }

    public List<Customer> getAll(){ return this.customerDAO.findAll(); }
}
