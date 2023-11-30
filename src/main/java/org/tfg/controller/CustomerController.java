package org.tfg.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger= LoggerFactory.getLogger(CustomerController.class);

    /*
    Este método es llamado mediante petición POST. Toma como entrada un MAP
    con la siguiente estructura: {
                                    "name":string,
                                    "email":string
                                 }
    Comprueba si el email tiene un formato adecuado, si es así, se lo pasa
    al CustomerService para registrarlo y guardarlo.
     */
    @PostMapping("/register")
    public void saveCustomer(@RequestBody Map<String, Object> info){
        logger.info("SAVING CUSTOMER: "+info.toString());
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");

        String name=info.get("name").toString();
        String email=info.get("email").toString();

        if(!pattern.matcher(email).find()){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Incorrect email");
        }

        this.customerService.save(name, email);
    }

    /*
    Este método es llamado mediante petición GET y se le pasa como PathVariable
    el id de un cliente. Su función es llamar al CustomerService y devolver dicho cliente.
     */
    @GetMapping("/getCustomerById/{id}")
    public Customer getCustomerById(@PathVariable String id){
        logger.info("GETTING CUSTOMER BY ID: "+id);
        return this.customerService.findCustomerById(id);
    }

    /*
    Este método es llamado mediante petición GET. Su función es llamar al CustomerService
    y devolver una lista con todos los clientes.
     */
    @GetMapping("/getCustomers")
    public List<Customer> getCustomers(){
        logger.info("GETTING ALL CUSTOMERS");
        return this.customerService.getAll();
    }

    /*
    Este método es llamado mediante petición PUT. Toma como entrada un Cliente.
    Esto se lo pasa al CustomerService para actualizar dicho cliente con los nuevos valores.
     */
    @PutMapping("/updateCustomer")
    public Customer update(@RequestBody Customer customer){
        logger.info("UPDATING CUSTOMER: "+customer.toString());
        return this.customerService.update(customer);
    }
    /*
    Este método es llamado mediante una petición PUT. Se le pasa como PathVariable
    el id del cliente y llama al CustomerService para cambiar su estado.
     */
    @PutMapping("/changeState/{customerId}")
    public Customer changeState(@PathVariable String customerId){
        logger.info("CHANGING CUSTOMER STATE: "+customerId);
        return this.customerService.changeState(customerId);
    }

}
