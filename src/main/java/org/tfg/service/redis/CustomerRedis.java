package org.tfg.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Customer;
import org.tfg.repository.CustomerDAO;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheManager = "redisCacheManager")
public class CustomerRedis {
    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Value("${server.port}")
    private int port;

    @CacheEvict(cacheNames = "customers", allEntries = true)
    public void save(Customer customer){
        String message=port+"/saveCustomer";
        redisTemplate.convertAndSend(topic.getTopic(),message);
        this.customerDAO.save(customer);
    }

    @Cacheable(cacheNames = "customer", key="#id", condition = "#id!=null")
    public Customer findCustomerById(String id){
        Optional<Customer> optCustomer=this.customerDAO.findById(id);
        if(optCustomer.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist");
        }
        return optCustomer.get();
    }

    @Cacheable(cacheNames = "customers")
    public List<Customer> getAll(){
        return this.customerDAO.findAll();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "companiesOrders", allEntries = true),
            @CacheEvict(cacheNames = "customersOrders", key = "#customer.id"),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "customers", allEntries = true)
    })
    @CachePut(cacheNames = "customer", key = "#customer.id", condition = "#customer.id!=null")
    public Customer update(Customer customer){
        String message=port+"/updateCustomer/"+customer.getId();
        redisTemplate.convertAndSend(topic.getTopic(),message);
        return this.customerDAO.save(customer);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "companiesOrders", allEntries = true),
            @CacheEvict(cacheNames = "customersOrders", key = "#customer.id"),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "customers", allEntries = true)
    })
    @CachePut(cacheNames = "customer", key = "#customer.id", condition = "#customer.id!=null")
    public Customer changeState(Customer customer){
        String message=port+"/changeCustomerState/"+customer.getId();
        redisTemplate.convertAndSend(topic.getTopic(), message);
        return this.customerDAO.save(customer);
    }
}
