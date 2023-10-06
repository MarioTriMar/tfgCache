package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tfg.config.CacheEntries;


import java.util.Map;


@RestController
@RequestMapping("cache")
@CrossOrigin("*")
public class CacheController {
    @Autowired
    private CacheEntries cacheEntries;

    /*
    Este método es llamado mediante petición GET y se le pasa como PathVariable
    el nombre de una caché. Su función es llamar al CacheEntries y devolver las entradas de
    dicha caché.
    */
    @GetMapping("/{cacheName}")
    public Map<Object, Object> getCompanyById(@PathVariable String cacheName){
        return this.cacheEntries.getAllEntriesInProductCache(cacheName);
    }
}
