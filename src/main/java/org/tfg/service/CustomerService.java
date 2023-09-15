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
    @Autowired
    private ControlMethods controlMethods;

    /*
    Este método recibe por parametros el nombre y el email del cliente.
    Crea el objeto Customer y le asigna el nombre, el email y que está activo.
    Guarda el cliente en la BBDD.
     */
    public void save(String name, String email){
        Customer customer=new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setEnabled(true);
        this.customerDAO.save(customer);
    }

    /*
    Este método recibe por parámetro el id del cliente.
    Busca dicho cliente en la BBDD, en caso de no estar lanza un 404.
    Si encuentra al usuario lo devuelve.
     */
    public Customer findCustomerById(String id) {
        Optional<Customer> optCustomer=this.customerDAO.findById(id);
        if(optCustomer.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist");
        }
        return optCustomer.get();
    }

    /*
    Este método devuelve la lista de clientes.
     */
    public List<Customer> getAll(){ return this.customerDAO.findAll(); }

    /*
    Este método recibe por parametro un cliente.
    Su función es actualizar el cliente.
     */
    public void update(Customer customer) {
        this.customerDAO.save(customer);
    }

    /*
    Este método recibe por parámetros el id del cliente.
    Su función es cambiar el estado en el que se encuentra el clietne.
    Para ello primero comprobará la existencia del cliente (llamando al
    método existCustomer de la clase ControlMethods), cambiará el estado si
    este existe y lo guardará.
     */
    public void changeState(String customerId) {
        Customer customer=this.controlMethods.existCustomer(customerId, false);
        customer.setEnabled(!customer.isEnabled());
        this.customerDAO.save(customer);
    }
}
