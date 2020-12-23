package com.sitech.crmmci.eai.brupet.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sitech.crmmci.eai.brupet.common.utils.ServletUtils;

@RestController
public class DispatcherController {
	private static final Logger logger = LoggerFactory.getLogger(DispatcherController.class);

	@ResponseBody
	@RequestMapping(value = "/pubCall")
	public String restPubCall(HttpServletRequest request) {
		logger.info("1:" + ServletUtils.readServletRequest(request));
		logger.info("2:" + ServletUtils.readServletRequest(request));
		return "OK";
	}

}
