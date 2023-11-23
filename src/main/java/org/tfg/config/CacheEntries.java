package org.tfg.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.HashMap;
import java.util.Map;


@Service
@EnableScheduling
public class CacheEntries {

    @Autowired
    private CacheManager cacheManager;

    private Logger logger= LoggerFactory.getLogger(CacheEntries.class);
    /*
    Este método recibe por parámetros el nombre de una de las cachés. Su función es
    devolver un Map con todos los datós de dicha caché.
     */
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

    @Scheduled(fixedRate = 5000)
    public void emptyCache() {
        logger.info("emptying cache");
        evictAllCaches();
    }
    public void evictAllCaches() {
        cacheManager.getCacheNames().stream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }
}

