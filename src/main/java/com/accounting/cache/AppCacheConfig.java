package com.accounting.cache;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class AppCacheConfig {

	@Autowired
	private CacheManager cacheManager;
	
	private Cache accountingLineCache;
	
	@PostConstruct
	public void init() {
		accountingLineCache = cacheManager.getCache("accounting-line");
	}
	
	public void putAccountingLineCache(String key, Map<String, ?> value) {
		accountingLineCache.put(key, value);
	}
	
	public ValueWrapper getAccountingLineCache(String key) {
		return accountingLineCache.get(key);
	}
}
