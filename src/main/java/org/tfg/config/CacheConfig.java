package org.tfg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.*;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.tfg.model.Company;
import org.tfg.model.Customer;
import org.tfg.model.Order;
import org.tfg.model.Product;
import org.tfg.subscriber.Receiver;


import java.time.Duration;
import java.util.List;


@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;

    public static final String MONEY = "money";
    public static final String PRODUCTS = "products";
    public static final String COMPANIES = "companies";
    public static final String CUSTOMERS = "customers";
    public static final String CUSTOMER = "customer";
    public static final String ORDERS = "orders";
    public static final String COMPANIES_ORDERS = "companiesOrders";
    public static final String CUSTOMERS_ORDERS = "customersOrders";
    public static final String ORDER = "order";
    public static final String COMPANY = "company";
    public static final String PRODUCT = "product";
    public static final long TTL=20;
    public static final String CHANNEL = "pubsub:cache-channel";

    @Bean
    public LettuceConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        return new LettuceConnectionFactory(configuration);
    }
    @Bean
    @Primary
    public CacheManager localCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(List.of(MONEY,PRODUCTS, COMPANIES, CUSTOMERS, CUSTOMER,
                ORDERS, COMPANIES_ORDERS, CUSTOMERS_ORDERS, ORDER, COMPANY, PRODUCT));
        return cacheManager;
    }

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .withCacheConfiguration(COMPANIES,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class))))
                .withCacheConfiguration(COMPANY,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Company>(Company.class))))
                .withCacheConfiguration(PRODUCTS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class))))
                .withCacheConfiguration(PRODUCT,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class))))
                .withCacheConfiguration(CUSTOMER,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Customer>(Customer.class))))
                .withCacheConfiguration(CUSTOMERS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class))))
                .withCacheConfiguration(ORDER,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Order>(Order.class))))
                .withCacheConfiguration(CUSTOMERS_ORDERS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class))))
                .withCacheConfiguration(COMPANIES_ORDERS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class))))
                .withCacheConfiguration(ORDERS,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new CustomRedisSerializer())))
                .withCacheConfiguration(MONEY,
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(TTL))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<Company>(Company.class))))
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new JdkSerializationRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }


    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    @Bean
    public ChannelTopic topic(){
        return new ChannelTopic(CHANNEL);
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(){
        return new MessageListenerAdapter(new Receiver());
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(){
        RedisMessageListenerContainer container=new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.addMessageListener(messageListenerAdapter(),topic());
        return container;
    }
}