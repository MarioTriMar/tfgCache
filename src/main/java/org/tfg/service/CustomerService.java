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
        redisTemplate.delete("customers");
        String message=port+"/saveCustomer";
        redisTemplate.convertAndSend(topic.getTopic(),message);
        this.customerDAO.save(customer);
    }

    /*
    Este método recibe por parámetro el id del cliente.
    Busca dicho cliente en la BBDD, en caso de no estar lanza un 404.
    Si encuentra al usuario lo devuelve.
     */
    @Cacheable(cacheNames = "customer", key="#id", condition = "#id!=null")
    public Customer findCustomerById(String id) throws JsonProcessingException {
        String key="customer::"+id;
        String customerRedis=(String) redisTemplate.opsForValue().get(key);
        if(customerRedis==null){
            Optional<Customer> optCustomer=this.customerDAO.findById(id);
            if(optCustomer.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist");
            }
            String customerJson = objectMapper.writeValueAsString(optCustomer.get());
            redisTemplate.opsForValue().set(key,customerJson, Duration.ofMinutes(22220));
            return optCustomer.get();
        }else{
            return objectMapper.readValue(customerRedis, new TypeReference<Customer>(){});
        }

    }

    /*
    Este método devuelve la lista de clientes.
     */
    @Cacheable(cacheNames = "customers")
    public List<Customer> getAll() throws JsonProcessingException {
        String key="customers";
        String customersRedis=(String)redisTemplate.opsForValue().get(key);
        if(customersRedis==null){
            List<Customer> customers=this.customerDAO.findAll();
            String customersListJson = objectMapper.writeValueAsString(customers);
            redisTemplate.opsForValue().set(key,customersListJson, Duration.ofMinutes(22220));
            return customers;
        }else{
            return objectMapper.readValue(customersRedis, new TypeReference<List<Customer>>() {});
        }
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
    public Customer update(Customer customer) throws JsonProcessingException {
        String key="customer::"+customer.getId();
        String customerJson=objectMapper.writeValueAsString(customer);
        redisTemplate.opsForValue().set(key,customerJson);
        String message=port+"/updateCustomer/"+customer.getId();
        redisTemplate.convertAndSend(topic.getTopic(),message);
        return this.customerDAO.save(customer);
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
    public Customer changeState(String customerId) throws JsonProcessingException {
        Customer customer=this.controlMethods.existCustomer(customerId, false);
        customer.setEnabled(!customer.isEnabled());
        String key="customer::"+customerId;
        String customerJson=objectMapper.writeValueAsString(customer);
        redisTemplate.opsForValue().set(key,customerJson);
        String message=port+"/changeCustomerState/"+customerId;
        redisTemplate.convertAndSend(topic.getTopic(), message);
        return this.customerDAO.save(customer);
    }
}
