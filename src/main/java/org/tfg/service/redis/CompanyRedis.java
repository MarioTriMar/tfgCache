package org.tfg.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.tfg.model.Company;
import org.tfg.model.Product;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.ProductDAO;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheManager = "redisCacheManager")
public class CompanyRedis {
    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Value("${server.port}")
    private int port;

    @CacheEvict(cacheNames="companies", allEntries = true)
    public void save(Company company){
        String message=port+"/saveCompany";
        redisTemplate.convertAndSend(topic.getTopic(), message);
        this.companyDAO.save(company);
    }

    @Cacheable(cacheNames = "company", key="#id", condition = "#id!=null")
    public Company findCompanyById(String id){
        Optional<Company> optCompany=this.companyDAO.findById(id);
        if(optCompany.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
        }
        return optCompany.get();
    }

    @Cacheable(cacheNames = "companies")
    public List<Company> getAll(){
        return this.companyDAO.findAll();
    }

    @Caching(evict={
            @CacheEvict(cacheNames="companies", allEntries = true),
            @CacheEvict(cacheNames="products", key="#company.id"),
            @CacheEvict(cacheNames="orders", allEntries = true),
            @CacheEvict(cacheNames="order", allEntries = true),
            @CacheEvict(cacheNames="companiesOrders", key = "#company.id"),
            @CacheEvict(cacheNames = "customersOrders", allEntries = true),
            @CacheEvict(cacheNames="products", key="'allProducts'"),
            @CacheEvict(cacheNames = "product", allEntries = true)
    })
    @CachePut(cacheNames="company", key="#company.id", condition = "#company.id!=null")
    public Company update(Company company){
        String message=port+"/updateCompany/"+company.getId();
        redisTemplate.convertAndSend(topic.getTopic(),message);
        return this.companyDAO.save(company);
    }

    @Cacheable(cacheNames = "products", key="#id", condition = "#id!=null", unless="#result == null")
    public List<Product> findCompanyProducts(String id){
        return this.productDAO.findByCompanyId(id);
    }

    @Caching(evict={
            @CacheEvict(cacheNames="companies", allEntries = true),
            @CacheEvict(cacheNames="products", key="#company.id"),
            @CacheEvict(cacheNames="orders", allEntries = true),
            @CacheEvict(cacheNames="order", allEntries = true),
            @CacheEvict(cacheNames="companiesOrders", key = "#company.id"),
            @CacheEvict(cacheNames = "customersOrders", allEntries = true),
            @CacheEvict(cacheNames="products", key="'allProducts'"),
            @CacheEvict(cacheNames = "product", allEntries = true)
    })
    @CachePut(cacheNames="company", key="#company.id", condition = "#companyId!=null")
    public Company changeState(Company company){
        String message=port+"/changeCompanyState/"+company.getId();
        redisTemplate.convertAndSend(topic.getTopic(), message);
        return this.companyDAO.save(company);
    }
}
