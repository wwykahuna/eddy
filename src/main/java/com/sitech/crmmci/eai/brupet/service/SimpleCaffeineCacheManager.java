package com.sitech.crmmci.eai.brupet.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.util.StringUtils;

import com.github.benmanes.caffeine.cache.Caffeine;

public class SimpleCaffeineCacheManager extends SimpleCacheManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCaffeineCacheManager.class);

	private Map<String, String> cacheSpecs = new HashMap<String, String>();

	public SimpleCaffeineCacheManager(Map<String, String> cacheSpecs) {
		super();
		if (cacheSpecs != null && !cacheSpecs.isEmpty()) {
			this.cacheSpecs.putAll(cacheSpecs);
		}
	}

	@Override
	protected Cache getMissingCache(String name) {
		String cfg = this.cacheSpecs.get(name);
		if (StringUtils.isEmpty(cfg)) {
			cfg = this.cacheSpecs.get("default");
			if (cfg == null) {
				cfg = "initialCapacity=5000,maximumSize=10000,expireAfterAccess=6000s";
			}
		}
		LOGGER.info("build cache : " + name + "=" + cfg);
		return new CaffeineCache(name, Caffeine.from(cfg).build());
	}
}
