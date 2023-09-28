package org.tfg.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;


@Service
public class CacheEntries {

    @Autowired
    private CacheManager cacheManager;

    public Map<Object, Object> getAllEntriesInProductCache(String cacheName) {
        Cache productCache = cacheManager.getCache(cacheName);
        Map<Object, Object> cacheEntries;
        if (productCache != null) {
            cacheEntries = (Map<Object, Object>) productCache.getNativeCache();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cache doesn't exist");
        }
        return cacheEntries;
    }
}

