package org.tfg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Customer;
import org.tfg.model.Product;
import org.tfg.repository.CustomerDAO;
import org.tfg.service.redis.CustomerRedis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private ControlMethods controlMethods;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Value("${server.port}")
    private int port;

    @Autowired
    private CustomerRedis customerRedis;

    private final ObjectMapper objectMapper=new ObjectMapper();

    /*
    Este método recibe por parametros el nombre y el email del cliente.
    Crea el objeto Customer y le asigna el nombre, el email y que está activo.
    Guarda el cliente en la BBDD.
     */
    @CacheEvict(cacheNames = "customers", allEntries = true)
    public void save(String name, String email){
        Customer customer=new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setEnabled(true);
        this.customerRedis.save(customer);
    }

    /*
    Este método recibe por parámetro el id del cliente.
    Busca dicho cliente en la BBDD, en caso de no estar lanza un 404.
    Si encuentra al usuario lo devuelve.
     */
    @Cacheable(cacheNames = "customer", key="#id", condition = "#id!=null")
    public Customer findCustomerById(String id){
        return this.customerRedis.findCustomerById(id);
    }

    /*
    Este método devuelve la lista de clientes.
     */
    @Cacheable(cacheNames = "customers")
    public List<Customer> getAll(){
        return this.customerRedis.getAll();
    }

    /*
    Este método recibe por parametro un cliente.
    Su función es actualizar el cliente.
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "companiesOrders", allEntries = true),
            @CacheEvict(cacheNames = "customersOrders", key = "#customer.id"),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "customers", allEntries = true)
    })
    @CachePut(cacheNames = "customer", key = "#customer.id", condition = "#customer.id!=null")
    public Customer update(Customer customer){
        return this.customerRedis.update(customer);
    }

    /*
    Este método recibe por parámetros el id del cliente.
    Su función es cambiar el estado en el que se encuentra el clietne.
    Para ello primero comprobará la existencia del cliente (llamando al
    método existCustomer de la clase ControlMethods), cambiará el estado si
    este existe y lo guardará.
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "companiesOrders", allEntries = true),
            @CacheEvict(cacheNames = "customersOrders", key = "#customerId"),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "customers", allEntries = true)
    })
    @CachePut(cacheNames = "customer", key = "#customerId", condition = "#customerId!=null")
    public Customer changeState(String customerId){
        Customer customer=this.controlMethods.existCustomer(customerId, false);
        customer.setEnabled(!customer.isEnabled());
        return this.customerRedis.changeState(customer);
    }
}
