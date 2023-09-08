package org.tfg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
import org.tfg.model.Customer;
import org.tfg.model.Order;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.CustomerDAO;
import org.tfg.repository.OrderDAO;

import java.util.List;
import java.util.Optional;

@Service
public class OrderSevice {

    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private CustomerDAO customerDAO;

    public void saveOrder(String companyId, String customerId, double price) {
        Order order=new Order();
        Optional<Company> optCompany = this.companyDAO.findById(companyId);
        Optional<Customer> optCustomer=this.customerDAO.findById(customerId);
        if(optCustomer.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist");
        }
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        order.setCompany(optCompany.get());
        order.setCustomer(optCustomer.get());
        order.setPrice(price);
        this.orderDAO.save(order);
    }

    public List<Order> getAll() {
        return this.orderDAO.findAll();
    }
}
