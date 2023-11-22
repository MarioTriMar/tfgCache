package org.tfg.service;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.tfg.model.Company;
import org.tfg.model.Customer;
import org.tfg.model.Product;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.ProductDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class CompanyService {
    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private ProductDAO productDAO;
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
    Este método recibe por parametros el nombre, el cif y el email de contacto
    de la compañía.
    Crea el objeto Company y le asigna el nombre, el cif, el email y que está activa.
    Guarda la compañía en la BBDD.
     */

    @CacheEvict(cacheNames="companies", allEntries = true)
    public void save(String name, String cif, String contactEmail){
        Company company=new Company();
        company.setName(name);
        company.setCif(cif);
        company.setContactEmail(contactEmail);
        company.setEnabled(true);
        redisTemplate.delete("companies");
        String message=port+"/saveCompany";
        redisTemplate.convertAndSend(topic.getTopic(), message);
        this.companyDAO.save(company);
    }

    /*
    Este método recibe por parámetro el id de la compañía.
    Busca dicha compañía en la BBDD, en caso de no estar lanza un 404.
    Si la encuentra la devuelve.
     */
    @Cacheable(cacheNames = "company", key="#id", condition = "#id!=null")
    public Company findCompanyById(String id) throws JsonProcessingException {
        String key="company::"+id;
        String companyRedis=(String) redisTemplate.opsForValue().get(key);
        if(companyRedis==null){
            Optional<Company> optCompany=this.companyDAO.findById(id);
            if(optCompany.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company doesn't exist");
            }
            String companyJson = objectMapper.writeValueAsString(optCompany.get());
            redisTemplate.opsForValue().set(key,companyJson, Duration.ofMinutes(22220));
            return optCompany.get();
        }else{
            return objectMapper.readValue(companyRedis, new TypeReference<Company>(){});
        }

    }

    /*
    Este método devuelve la lista de compañías.
     */
    @Cacheable(cacheNames = "companies")
    public List<Company> getAll() throws JsonProcessingException {
        String key="companies";
        String companiesRedis=(String)redisTemplate.opsForValue().get(key);
        if(companiesRedis==null){
            List<Company> companies=this.companyDAO.findAll();
            String companiesListJson=objectMapper.writeValueAsString(companies);
            redisTemplate.opsForValue().set(key,companiesListJson);
            return companies;
        }else{
            return objectMapper.readValue(companiesRedis, new TypeReference<List<Company>>() {});
        }
    }

    /*
    Este método recibe por parametro una compañía.
    Su función es actualizarla.
     */
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
    public Company update(Company company) throws JsonProcessingException {
        String key="company::"+company.getId();
        String companyJson=objectMapper.writeValueAsString(company);
        redisTemplate.opsForValue().set(key,companyJson);
        this.cleanCache(company.getId());
        String message=port+"/updateCompany/"+company.getId();
        redisTemplate.convertAndSend(topic.getTopic(),message);
        return this.companyDAO.save(company);
    }

    private void cleanCache(String companyId) {
        redisTemplate.delete("companies");
        redisTemplate.delete("products::"+companyId);
        redisTemplate.delete("orders");
        redisTemplate.delete("order");
        redisTemplate.delete("companiesOrders::"+companyId);
        redisTemplate.delete("customersOrders");
        redisTemplate.delete("products::allProducts");
        redisTemplate.delete("product");
    }

    /*
    Este método recibe por parámetros el id de una compañía.
    Su función es listar todos los productos de dicha compañía.
    Primero comprobará la existencia de dicha compañía (llamando al método
    existCompany de la clase ControlMethods).
     */
    @Cacheable(cacheNames = "products", key="#id", condition = "#id!=null", unless="#result == null")
    public List<Product> findCompanyProducts(String id) throws JsonProcessingException {
        String key="products::"+id;
        List<Product> products= new ArrayList<>();
        String productsRedis= (String) redisTemplate.opsForValue().get(key);
        ObjectMapper objectMapper=new ObjectMapper();
        if(productsRedis==null){
            Company company=this.controlMethods.existCompany(id, false);
            products= productDAO.findByCompanyId(company.getId());
            String productListJson = objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue().set(key, productListJson);
            return products;
        }else{
            return objectMapper.readValue(productsRedis, new TypeReference<List<Product>>() {});
        }
    }

    /*
    Este método recibe por parámetros el id de una compañía.
    Su función es cambiar el estado en el que esta se encuentra.
    Primero comprobará la existencia de esta.
     */
    @Caching(evict={
            @CacheEvict(cacheNames="companies", allEntries = true),
            @CacheEvict(cacheNames="products", key="#companyId"),
            @CacheEvict(cacheNames="orders", allEntries = true),
            @CacheEvict(cacheNames="order", allEntries = true),
            @CacheEvict(cacheNames="companiesOrders", key = "#companyId"),
            @CacheEvict(cacheNames = "customersOrders", allEntries = true),
            @CacheEvict(cacheNames="products", key="'allProducts'"),
            @CacheEvict(cacheNames = "product", allEntries = true)
    })
    @CachePut(cacheNames="company", key="#companyId", condition = "#companyId!=null")
    public Company changeState(String companyId) throws JsonProcessingException {
        Company company=this.controlMethods.existCompany(companyId, false);
        company.setEnabled(!company.isEnabled());
        String key="company::"+companyId;
        String companyJson=objectMapper.writeValueAsString(company);
        redisTemplate.opsForValue().set(key, companyJson);
        this.cleanCache(company.getId());
        String message=port+"/changeCompanyState/"+companyId;
        redisTemplate.convertAndSend(topic.getTopic(), message);
        return this.companyDAO.save(company);
    }
}
