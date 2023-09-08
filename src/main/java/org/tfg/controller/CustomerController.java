package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Customer;
import org.tfg.service.CustomerService;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("customers")
@CrossOrigin("*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @PostMapping("/register")
    public void saveCompany(@RequestBody Map<String, Object> info){
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");

        String name=info.get("name").toString();
        String email=info.get("email").toString();

        if(!pattern.matcher(email).find()){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Incorrect email");
        }

        this.customerService.save(name, email);
    }

    @GetMapping("/getCustomers")
    public List<Customer> getCustomers(){  return this.customerService.getAll();}

    @PutMapping("/updateCustomer")
    public void update(@RequestBody Customer customer){
        this.customerService.update(customer);
    }

    @DeleteMapping("/deleteCustomer/{customerId}")
    public void deleteCustomer(@PathVariable String companyId){
        //qué borrar??
    }
}
