package com.sitech.crmmci.eai.brupet.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.sitech.crmmci.eai.brupet.config.properties.CaffeineCacheProperties;
import com.sitech.crmmci.eai.brupet.service.SimpleCaffeineCacheManager;

@EnableCaching
@Configuration
@EnableConfigurationProperties(CaffeineCacheProperties.class)
public class CaffeineCacheConfiguration {
	@Autowired
	private CaffeineCacheProperties cacheProperties;

	@Bean
	public CacheManager cacheManager() {
		Map<String, String> cacheSpecs = cacheProperties.getSpecMap();
		SimpleCaffeineCacheManager manager = new SimpleCaffeineCacheManager(cacheSpecs);
		if (cacheSpecs != null && !cacheSpecs.isEmpty()) {
			List<CaffeineCache> caches = cacheSpecs.entrySet().stream()
					.map(entry -> new CaffeineCache(entry.getKey(), Caffeine.from(entry.getValue()).build()))
					.collect(Collectors.toList());
			manager.setCaches(caches);
		}
		return manager;
	}
}
