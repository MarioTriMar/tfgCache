package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Customer;
import org.tfg.repository.CustomerDAO;

import java.util.List;
import java.util.Optional;

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
    public Customer findCustomerById(String id) {
        Optional<Customer> optCustomer=this.customerDAO.findById(id);
        if(optCustomer.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist");
        }
        return optCustomer.get();
    }
    public List<Customer> getAll(){ return this.customerDAO.findAll(); }

    public void update(Customer customer) {
        this.customerDAO.save(customer);
    }


}
