package com.sitech.crmmci.eai.brupet.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

@Configuration
@MapperScan("com.sitech.crmmci.eai.brupet.mybatis.mapper.*")
public class MybatisPlusConfiguration {
	
	@Bean
	public MybatisPlusInterceptor paginationInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.ORACLE_12C));
		return interceptor;
	}
}
