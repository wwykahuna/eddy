package com.sitech.crmmci.eai.brupet.common.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private final byte[] body;

	public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		String reqMethod = request.getMethod();
		if ("GET".equals(reqMethod)) {
			body = request.getQueryString().getBytes();
		} else {
			body = getBodyString(request).getBytes();
		}
	}

	public String getBodyString(final HttpServletRequest request) throws IOException {
		String contentType = request.getContentType();
		String bodyString = "";
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(contentType)
				&& (contentType.contains("multipart/form-data") || contentType.contains("x-www-form-urlencoded"))) {
			Map<String, String[]> parameterMap = request.getParameterMap();
			for (Map.Entry<String, String[]> next : parameterMap.entrySet()) {
				String[] values = next.getValue();
				String value = null;
				if (values != null) {
					if (values.length == 1) {
						value = values[0];
					} else {
						value = Arrays.toString(values);
					}
				}
				sb.append(next.getKey()).append("=").append(value).append("&");
			}
			if (sb.length() > 0) {
				bodyString = sb.toString().substring(0, sb.toString().length() - 1);
			}
			return bodyString;
		} else {
			return IOUtils.toString(request.getInputStream(), Charset.forName("UTF-8"));
		}
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {

		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

		return new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
			}
		};
	}
}
