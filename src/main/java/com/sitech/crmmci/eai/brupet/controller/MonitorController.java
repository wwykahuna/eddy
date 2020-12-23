package com.sitech.crmmci.eai.brupet.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.actuate.env.EnvironmentEndpoint.PropertySourceDescriptor;
import org.springframework.boot.actuate.env.EnvironmentEndpoint.PropertyValueDescriptor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitorController {

	private static final Logger logger = LoggerFactory.getLogger(MonitorController.class);

	@Autowired
	private HealthEndpoint healthEndpoint;

	@Autowired
	private EnvironmentEndpoint environmentEndpoint;

	@Autowired
	private MetricsEndpoint metricsEndpoint;

	@ResponseBody
	@RequestMapping(value = "/monitor")
	public String servQry() {
		logger.info("healthEndpoint = " + healthEndpoint.health().getStatus());
		List<PropertySourceDescriptor> list = environmentEndpoint.environment("").getPropertySources();
		for (PropertySourceDescriptor source : list) {
			logger.info(source.getName() + ":");
			for (Map.Entry<String, PropertyValueDescriptor> entry : source.getProperties().entrySet()) {
				logger.info("\t" + entry.getKey() + " = " + entry.getValue().getValue());
			}
		}

		for (String metrceName : metricsEndpoint.listNames().getNames()) {
			MetricResponse response = metricsEndpoint.metric(metrceName, null);
			logger.info(response.getName() + ":" + response.getMeasurements());
		}
		return "OK";
	}
}
