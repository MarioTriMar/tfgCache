package org.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tfg.config.CacheEntries;
import org.tfg.model.Company;

import java.util.Map;


@RestController
@RequestMapping("cache")
@CrossOrigin("*")
public class CacheController {
    @Autowired
    private CacheEntries cacheEntries;

    @GetMapping("/{cacheName}")
    public Map<Object, Object> getCompanyById(@PathVariable String cacheName){
        return this.cacheEntries.getAllEntriesInProductCache(cacheName);
    }
}
