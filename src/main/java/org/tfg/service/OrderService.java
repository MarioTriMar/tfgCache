package org.tfg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.*;
import org.tfg.repository.OrderDAO;


import java.sql.Timestamp;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderDAO orderDAO;
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
    Este método recibe por parámetros el id de la compañía, el id del cliente y
    la lista de productos.
    Primero crea un id y la fecha de creación. Después comprueba si existen y si están
    activadas la compañia y el cliente.
    Una vez hecho esto recorre la lista de productos comprobando si estos existen y si están
    en stock.
    Llama al método belongsProductToCompany para saber si la lista de productos pertenecen a la
    compañía.
    Si cumple todas las condiciones llama al método makeOrder.
     */
    @Caching(evict={
            @CacheEvict(cacheNames = "companiesOrders", key="#companyId"),
            @CacheEvict(cacheNames = "customersOrders", key="#customerId"),
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "money", key="#customerId")
    })
    public void saveOrder(String companyId, String customerId, List<String> products) throws JsonProcessingException {
        String id= UUID.randomUUID().toString();
        Timestamp creationTime=new Timestamp(System.currentTimeMillis());
        Company company=this.controlMethods.existCompany(companyId, true);
        Customer customer=this.controlMethods.existCustomer(customerId, true);
        List<Product> productList=new ArrayList<>();
        for(int i=0;i<products.size();i++){
            Product product=this.controlMethods.existProduct(products.get(i), true);
            productList.add(product);
        }
        if(!this.controlMethods.belongsProductToCompany(companyId, productList)){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Products doesn't belong to the company");
        }
        this.makeOrder(id, creationTime, company, customer, productList);
    }

    /*
    Este método recibe por parámetros el id del pedido, la fecha de creación, la compañía,
    el cliente y la lista de productos.
    Es llamado por el método anterior y su función es crear el objeto Order y guardarlo
    en la BBDD.
     */
    private void makeOrder(String id, Timestamp creationTime, Company company, Customer customer,
                          List<Product> productList){
        Order order=new Order();
        order.setId(id);
        order.setCreationTime(creationTime);
        order.setCompany(company);
        order.setCustomer(customer);
        order.setProduct(productList);
        for(int i=0;i<order.getProduct().size();i++){
            order.setPrice(order.getPrice()+order.getProduct().get(i).getPrice());
        }
        this.cleanCache(company.getId(), customer.getId());
        this.orderDAO.save(order);
    }

    private void cleanCache(String companyId, String customerId) {
        redisTemplate.delete("companiesOrders::"+companyId);
        redisTemplate.delete("customersOrders::"+customerId);
        redisTemplate.delete("orders");
        redisTemplate.delete("order");
        redisTemplate.delete("money::"+customerId);

        String message=port+"/saveOrder/"+companyId+"/"+customerId;
        redisTemplate.convertAndSend(topic.getTopic(),message);
    }

    /*
    Este método devuelve una lista con todos los pedidos.
     */
    @Cacheable(cacheNames = "orders", key = "'allOrders'")
    public List<Order> getAll() throws JsonProcessingException {
        String key="orders";
        String ordersRedis=(String)redisTemplate.opsForValue().get(key);
        if(ordersRedis==null){
            List<Order> orders=this.orderDAO.findAll();
            String ordersListJson=objectMapper.writeValueAsString(orders);
            redisTemplate.opsForValue().set(key,ordersListJson);
            return orders;
        }else{
            return objectMapper.readValue(ordersRedis, new TypeReference<List<Order>>() {});
        }
    }

    /*
    Este método recibe por parámetros el id de un pedido.
    Su función es buscar en la BBDD el pedido y devolverlo.
    En caso se no existir lanzará un 404.
     */
    @Cacheable(cacheNames = "order", key = "#id", condition = "#id!=null")
    public Order findOrderById(String id) throws JsonProcessingException {
        String key="order::"+id;
        String orderRedis=(String)redisTemplate.opsForValue().get(key);
        if(orderRedis==null){
            Optional<Order> optOrder=this.orderDAO.findById(id);
            if(optOrder.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order doesn't exist");
            }
            String orderJson=objectMapper.writeValueAsString(optOrder.get());
            redisTemplate.opsForValue().set(key,orderJson);
            return optOrder.get();
        }else{
            return objectMapper.readValue(orderRedis, new TypeReference<Order>(){});
        }

    }

    /*
    Este método recibe por parámetros el id de una compañía.
    Primero comprobará si existe la compañía. Si es así devolverá la lista
    de pedidos de una compañía.
     */
    @Cacheable(cacheNames = "companiesOrders", key="#companyId", condition = "#companyId!=null")
    public List<Order> findByCompanyId(String companyId) throws JsonProcessingException {
        String key="companiesOrders::"+companyId;
        String ordersRedis=(String) redisTemplate.opsForValue().get(key);
        if(ordersRedis==null){
            Company company=this.controlMethods.existCompany(companyId, false);
            List<Order> orders= this.orderDAO.findByCompany(company);
            String ordersListJson=objectMapper.writeValueAsString(orders);
            redisTemplate.opsForValue().set(key,ordersListJson);
            return orders;
        }else{
            return objectMapper.readValue(ordersRedis, new TypeReference<List<Order>>(){});
        }

    }

    /*
    Este método recibe por parámetros el id de un cliente.
    Primero comprobará si existe el cliente. Si es así devolverá la lista
    de pedidos de un cliente.
     */
    @Cacheable(cacheNames="customersOrders", key="#customerId", condition = "#customerId!=null")
    public List<Order> findByCustomerId(String customerId) throws JsonProcessingException {
        String key="customersOrders::"+customerId;
        String ordersRedis=(String) redisTemplate.opsForValue().get(key);
        if(ordersRedis==null){
            Customer customer=this.controlMethods.existCustomer(customerId, false);
            List<Order> orders= this.orderDAO.findByCustomer(customer);
            String ordersListJson=objectMapper.writeValueAsString(orders);
            redisTemplate.opsForValue().set(key,ordersListJson);
            return orders;
        }else{
            return objectMapper.readValue(ordersRedis, new TypeReference<List<Order>>(){});
        }
    }

    /*
    Este método recibe por parámetros el id de un cliente.
    Primero comprobará si existe el cliente. Si es así devolverá la lista
    de pedidos de un cliente y calculará el dinero gastado por el cliente.
     */
    @Cacheable(cacheNames="money", key="#customerId", condition = "#customerId!=null")
    public double getTotalMoney(String customerId) throws JsonProcessingException {
        String key="money::"+customerId;
        String money = (String) redisTemplate.opsForValue().get(key);
        double total=0;
        if(money==null){
            List<Order> orders=this.findByCustomerId(customerId);
            for (Order order : orders) {
                total += order.getPrice();
            }
            redisTemplate.opsForValue().set(key, total);
        }else{
            total=Double.parseDouble(money);
        }

        return total;
    }
}
