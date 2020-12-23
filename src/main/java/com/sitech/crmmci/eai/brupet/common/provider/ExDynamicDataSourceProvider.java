package com.sitech.crmmci.eai.brupet.common.provider;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.sitech.crmmci.eai.brupet.common.service.DBDecryption;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ExDynamicDataSourceProvider implements DynamicDataSourceProvider {

	private DynamicDataSourceProperties properties;
	private DBDecryption dbDecryption;

	public ExDynamicDataSourceProvider(DynamicDataSourceProperties properties, DBDecryption dbDecryption) {
		this.properties = properties;
		this.dbDecryption = dbDecryption;
	}

	@Override
	public Map<String, DataSource> loadDataSources() {
		Map<String, DataSourceProperty> dataSourcePropertiesMap = properties.getDatasource();
		return createDataSourceMap(dataSourcePropertiesMap);
	}

	protected Map<String, DataSource> createDataSourceMap(Map<String, DataSourceProperty> dataSourcePropertiesMap) {
		Map<String, DataSource> dataSourceMap = new HashMap<>(dataSourcePropertiesMap.size() * 2);
		for (Map.Entry<String, DataSourceProperty> item : dataSourcePropertiesMap.entrySet()) {
			DataSourceProperty dataSourceProperty = item.getValue();
			String pollName = dataSourceProperty.getPoolName();
			if (pollName == null || "".equals(pollName)) {
				pollName = item.getKey();
			}
			dataSourceProperty.setPoolName(pollName);
			dataSourceMap.put(pollName, createDataSource(dataSourceProperty));
		}
		return dataSourceMap;
	}

	public DataSource createDataSource(DataSourceProperty dataSourceProperty) {
		HikariConfig config = dataSourceProperty.getHikari().toHikariConfig(properties.getHikari());
		dbDecryption.init(dataSourceProperty.getUsername(), dataSourceProperty.getPassword());
		config.setUsername(dbDecryption.getDencryptUser());
		config.setPassword(dbDecryption.getDencryptPass());
		config.setJdbcUrl(dataSourceProperty.getUrl());
		config.setDriverClassName(dataSourceProperty.getDriverClassName());
		config.setPoolName(dataSourceProperty.getPoolName());
		return new HikariDataSource(config);
	}

}
