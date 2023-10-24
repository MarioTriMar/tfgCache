package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.tfg.config.CacheEntries;


//Singleton para gestionar las acciones de los eventos.
@Component
public class Manager {

    @Value("${server.port}")
    private String port;
    @Autowired
    private CacheEntries cacheEntries;

    private static class ManagerHolder {
        static Manager singleton=new Manager();
    }
    @Bean
    public static Manager get() {
        return ManagerHolder.singleton;
    }

    public CacheEntries getCacheEntries(){
        return this.cacheEntries;
    }

    public String getPort(){
        return port;
    }
}