package com.sitech.crmmci.eai.brupet.common.utils;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletUtils {

	private static final Logger logger = LoggerFactory.getLogger(ServletUtils.class);

	private ServletUtils() {
	}

	public static String readServletRequest(ServletRequest request) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		String inputLine;
		try {
			br = request.getReader();
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
		} catch (IOException e) {
			logger.error("read ServletRequest io error <" + request.getCharacterEncoding() + ">", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}
}
