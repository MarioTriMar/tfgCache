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
import org.tfg.model.Company;
import org.tfg.model.Product;
import org.tfg.repository.CompanyDAO;
import org.tfg.repository.ProductDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CompanyDAO companyDAO;
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
    Este método recibe por parámetros el nombre, los detalles y el precio de la compañía
    junto con el id de la compañía a la que pertenece.
    Primero comprueba si dicha empresa existe y si está activa, para no añadir un producto a una
    empresa que no está activa.
    Crea el objeto Product y lo guarda en la BBDD.
     */
    @Caching(evict={
            @CacheEvict(cacheNames = "products", key="'allProducts'"),
            @CacheEvict(cacheNames = "products", key="#companyId")
    })
    public void save(String name, String details, double price, String companyId) {
        Company company=this.controlMethods.existCompany(companyId, true);
        Product product=new Product();
        product.setName(name);
        product.setDetails(details);
        product.setPrice(price);
        product.setCompany(company);
        product.setStock(true);
        redisTemplate.delete("products::allProducts");
        redisTemplate.delete("products::"+companyId);
        String message=port+"/saveProduct/"+companyId;
        redisTemplate.convertAndSend(topic.getTopic(), message);
        this.productDAO.save(product);
    }
    /*
    Este método recibe por parámetro el id, el nombre, los detalles, el precio
    y el stock de un producto junto con el id de la compañía a la que pertenece.
    Primero comprueba si existen y si están activos o en stock la compañía y el producto.
    Después actualiza los valores y guarda el producto.
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "companiesOrders", key = "#companyId"),
            @CacheEvict(cacheNames = "customersOrders", allEntries = true),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "products", key = "#companyId"),
            @CacheEvict(cacheNames = "products", key = "'allProducts'")
    })
    @CachePut(cacheNames = "product", key = "#id", condition = "#id!=null")
    public Product updateProduct(String id, String name, String details, double price, boolean stock, String companyId) throws JsonProcessingException {
        Company company=this.controlMethods.existCompany(companyId, true);
        Product product=this.controlMethods.existProduct(id, true);
        product.setCompany(company);
        product.setDetails(details);
        product.setPrice(price);
        product.setName(name);
        product.setStock(stock);
        String key="product::"+id;
        String productJson=objectMapper.writeValueAsString(product);
        redisTemplate.opsForValue().set(key,productJson);
        this.cleanCache(id);
        String message=port+"/updateProduct/"+id;
        redisTemplate.convertAndSend(topic.getTopic(),message);
        return this.productDAO.save(product);
    }

    private void cleanCache(String companyId) {
        redisTemplate.delete("orders");
        redisTemplate.delete("companiesOrders::"+companyId);
        redisTemplate.delete("customersOrders");
        redisTemplate.delete("order");
        redisTemplate.delete("products::"+companyId);
        redisTemplate.delete("products::allProducts");
    }

    /*
    Este método devuelve la lista con todos los productos.
     */
    @Cacheable(cacheNames = "products", key = "'allProducts'")
    public List<Product> getAll() throws JsonProcessingException {
        String key="products::allProducts";
        String productsRedis=(String) redisTemplate.opsForValue().get(key);
        if(productsRedis==null){
            List<Product> products=this.productDAO.findAll();
            String productsListJson=objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue().set(key, productsListJson);
            return products;
        }else{
            return objectMapper.readValue(productsRedis, new TypeReference<List<Product>>() {});
        }
    }

    /*
    Este método recibe por parámetros el id del producto.
    Su función es cambiar el estado del stock. Primero comprueba si el producto
    existe.
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "orders", allEntries = true),
            @CacheEvict(cacheNames = "order", allEntries = true),
            @CacheEvict(cacheNames = "companiesOrders", key = "#companyId"),
            @CacheEvict(cacheNames = "customersOrders", allEntries = true),
            @CacheEvict(cacheNames = "products", key = "#companyId"),
            @CacheEvict(cacheNames = "products", key = "'allProducts'")
    })
    @CachePut(cacheNames = "product", key = "#productId", condition = "#productId!=null")
    public Product changeStock(String productId, String companyId) throws JsonProcessingException {
        List<Product> productList=new ArrayList<>();
        Product product=this.controlMethods.existProduct(productId, false);
        productList.add(product);
        if(!this.controlMethods.belongsProductToCompany(companyId, productList)){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Products doesn't belong to the company");
        }
        product.setStock(!product.isStock());
        String key="product::"+productId;
        String productJson=objectMapper.writeValueAsString(product);
        redisTemplate.opsForValue().set(key,productJson);
        this.cleanCache(companyId);
        String message=port+"/changeStock/"+companyId;
        redisTemplate.convertAndSend(topic.getTopic(),message);
        return this.productDAO.save(product);
    }

    /*
    Este método recibe por parámetro el id del producto.
    Busca dicho producto en la BBDD, en caso de no estar lanza un 404.
    Si lo encuentra lo devuelve.
     */
    @Cacheable(cacheNames = "product", key="#id", condition = "#id!=null")
    public Product getProductById(String id) throws JsonProcessingException {
        String key="product::"+id;
        String productRedis=(String) redisTemplate.opsForValue().get(key);
        if(productRedis==null){
            Optional<Product> optProduct=this.productDAO.findById(id);
            if(optProduct.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product doesn't exist");
            }
            String productJson = objectMapper.writeValueAsString(optProduct.get());
            redisTemplate.opsForValue().set(key,productJson);
            return optProduct.get();
        }else{
            return objectMapper.readValue(productRedis, new TypeReference<Product>(){});
        }

    }
}
