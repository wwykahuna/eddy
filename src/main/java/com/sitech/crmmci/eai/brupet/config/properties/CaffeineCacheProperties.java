package com.sitech.crmmci.eai.brupet.config.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache.caffeine")
public class CaffeineCacheProperties {
	private Map<String, String> specMap;

	public Map<String, String> getSpecMap() {
		return specMap;
	}

	public void setSpecMap(Map<String, String> specMap) {
		this.specMap = specMap;
	}

}