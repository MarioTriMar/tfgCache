package org.tfg.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.tfg.model.Company;
import org.tfg.model.Product;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.ProductDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tfg.service.redis.CompanyRedis;
import java.util.List;



@Service
public class CompanyService {
    @Autowired
    private CompanyDAO companyDAO;
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private ControlMethods controlMethods;

    @Autowired
    private CompanyRedis companyRedis;
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
        this.companyRedis.save(company);
    }

    /*
    Este método recibe por parámetro el id de la compañía.
    Busca dicha compañía en la BBDD, en caso de no estar lanza un 404.
    Si la encuentra la devuelve.
     */
    @Cacheable(cacheNames = "company", key="#id", condition = "#id!=null")
    public Company findCompanyById(String id){
        return this.companyRedis.findCompanyById(id);
    }

    /*
    Este método devuelve la lista de compañías.
     */
    @Cacheable(cacheNames = "companies")
    public List<Company> getAll(){
        return this.companyRedis.getAll();
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
    public Company update(Company company){
        return this.companyRedis.update(company);
    }

    /*
    Este método recibe por parámetros el id de una compañía.
    Su función es listar todos los productos de dicha compañía.
    Primero comprobará la existencia de dicha compañía (llamando al método
    existCompany de la clase ControlMethods).
     */
    @Cacheable(cacheNames = "products", key="#id", condition = "#id!=null", unless="#result == null")
    public List<Product> findCompanyProducts(String id){
        Company company=this.controlMethods.existCompany(id, false);
        return this.companyRedis.findCompanyProducts(id);
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
    public Company changeState(String companyId){
        Company company=this.controlMethods.existCompany(companyId, false);
        company.setEnabled(!company.isEnabled());
        return this.companyRedis.changeState(company);
    }
}
