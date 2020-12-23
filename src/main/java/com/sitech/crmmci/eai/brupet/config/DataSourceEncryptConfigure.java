package com.sitech.crmmci.eai.brupet.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.sitech.crmmci.eai.brupet.common.provider.ExDynamicDataSourceProvider;
import com.sitech.crmmci.eai.brupet.common.service.DBDecryption;
import com.sitech.crmmci.eai.brupet.common.service.SimpleDBDecryption;

@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class DataSourceEncryptConfigure {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceEncryptConfigure.class);
	
	@Autowired
	private DynamicDataSourceProperties properties;

	@Value("${spring.datasource.encrypt:''}")
	private String encryptMethod;
	
	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Bean
	public DynamicDataSourceProvider dynamicDataSourceProvider() {
		DBDecryption enService = null;

		Map<String, DBDecryption> services = applicationContext.getBeansOfType(DBDecryption.class);
		if (encryptMethod != null && !encryptMethod.equals("")) {
			for (DBDecryption service : services.values()) {
				if (service.getEncryptName().equals(encryptMethod)) {
					enService = service;
				}
			}
		}
		
		if (enService == null) {
			enService = new SimpleDBDecryption();
		}
		
		LOGGER.info("encryptMethod = " + encryptMethod + ",enService=" + enService);
		return new ExDynamicDataSourceProvider(properties, enService);
	}
}
