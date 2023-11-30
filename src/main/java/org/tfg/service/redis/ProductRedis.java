package org.tfg.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Product;
import org.tfg.repository.ProductDAO;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheManager = "redisCacheManager")
public class ProductRedis {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Value("${server.port}")
    private int port;

    @Caching(evict={
            @CacheEvict(cacheNames = "products", key="'allProducts'"),
            @CacheEvict(cacheNames = "products", key="#product.company.id")
    })
    public Product save(Product product){
        String message=port+"/saveProduct/"+product.getCompany().getId();
        redisTemplate.convertAndSend(topic.getTopic(), message);
        return this.productDAO.save(product);
    }
    @Caching(evict = {
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "companiesOrders", key = "#product.company.id"),
            @CacheEvict(cacheNames = "customersOrders", allEntries = true),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "products", key = "#product.company.id"),
            @CacheEvict(cacheNames = "products", key = "'allProducts'")
    })
    @CachePut(cacheNames = "product", key = "#product.id", condition = "#id!=null")
    public Product updateProduct(Product product){
        String message=port+"/updateProduct/"+product.getId();
        redisTemplate.convertAndSend(topic.getTopic(),message);
        return this.productDAO.save(product);
    }

    @Cacheable(cacheNames = "products", key="'allProducts'")
    public List<Product> getAll(){
        return this.productDAO.findAll();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "companiesOrders", key = "#product.company.id"),
            @CacheEvict(cacheNames = "customersOrders", allEntries = true),
            @CacheEvict(cacheNames = "products", key = "#product.company.id"),
            @CacheEvict(cacheNames = "products", key = "'allProducts'")
    })
    @CachePut(cacheNames = "product", key = "#product.id", condition = "#product.id!=null")
    public Product changeStock(Product product){
        String message=port+"/changeStock/"+product.getCompany().getId();
        redisTemplate.convertAndSend(topic.getTopic(),message);
        return this.productDAO.save(product);
    }

    @Cacheable(cacheNames = "product", key="#id", condition = "#id!=null")
    public Product getProductById(String id){
        Optional<Product> optProduct= this.productDAO.findById(id);
        if(optProduct.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product doesn't exist");
        }
        return optProduct.get();
    }
}
