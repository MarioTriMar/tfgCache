package org.tfg.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
import org.tfg.model.Customer;
import org.tfg.model.Order;
import org.tfg.repository.OrderDAO;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheManager = "redisCacheManager")
public class OrderRedis {
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Value("${server.port}")
    private int port;

    @Caching(evict={
            @CacheEvict(cacheNames = "companiesOrders", key="#order.company.id"),
            @CacheEvict(cacheNames = "customersOrders", key="#order.customer.id"),
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "money", key="#order.customer.id")
    })
    public void saveOrder(Order order){
        String message=port+"/saveCustomer";
        redisTemplate.convertAndSend(topic.getTopic(),message);
        this.orderDAO.save(order);
    }

    @Cacheable(cacheNames = "orders", key = "'allOrders'")
    public List<Order> getAll(){
        return this.orderDAO.findAll();
    }

    @Cacheable(cacheNames = "order", key = "#id", condition = "#id!=null")
    public Object findOrderById(String id){
        Optional<Order> optOrder=this.orderDAO.findById(id);
        if(optOrder.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order doesn't exist");
        }
        return optOrder.get();
    }

    @Cacheable(cacheNames = "companiesOrders", key="#company.id", condition = "#company.id!=null")
    public List<Order> findByCompanyId(Company company){
        return this.orderDAO.findByCompany(company);
    }

    @Cacheable(cacheNames="customersOrders", key="#customer.id", condition = "#customer.id!=null")
    public List<Order> findByCustomerId(Customer customer){
        return this.orderDAO.findByCustomer(customer);
    }

    @Cacheable(cacheNames="money", key="#customer.id", condition = "#customer.id!=null")
    public double getTotalMoney(Customer customer){
        double total=0;
        List<Order> orders=this.findByCustomerId(customer);
        for (Order order : orders) {
            total += order.getPrice();
        }
        return total;
    }
}
