package org.tfg.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;


@Component
@EnableScheduling
public class CacheEntries {

    @Autowired
    @Qualifier("localCacheManager")
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

    public void evict(String cacheName, String key) {
        Cache cache=cacheManager.getCache(cacheName);
        if (cache!=null)
            cache.evict(key);
    }

    public void clean(String cacheName) {
        Cache cache=cacheManager.getCache(cacheName);
        if(cache!=null)
            cache.clear();
    }

    public void update(String cacheName, String key, Object object){
        Cache cache=cacheManager.getCache(cacheName);
        if(cache!=null)
            cache.put(key, object);
    }

    //Cada 5 minutos
    @Scheduled(fixedRate = 300000)
    public void evictAllCaches() {
        logger.info("Cleaning cache");
        cacheManager.getCacheNames().stream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }
   
}

