package org.tfg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.cache.interceptor.CacheErrorHandler;


import java.time.Duration;


@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    @Bean
    public LettuceConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        return new LettuceConnectionFactory(configuration);
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new JdkSerializationRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }



    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("companies",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("company",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("products",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("product",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("customer",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("customers",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("order",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("customersOrders",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("companiesOrders",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("orders",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)));
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }
}