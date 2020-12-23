package com.sitech.crmmci.eai.brupet.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitech.crmmci.eai.brupet.common.filter.RepeatReadFilter;

@Configuration
public class FilterConfiguration {

	@Bean
	public FilterRegistrationBean<RepeatReadFilter> registFilter() {
		FilterRegistrationBean<RepeatReadFilter> registration = new FilterRegistrationBean<RepeatReadFilter>();
		registration.setFilter(new RepeatReadFilter());
		registration.addUrlPatterns("/*");
		registration.setName("UrlFilter");
		registration.setOrder(1);
		return registration;
	}
}
