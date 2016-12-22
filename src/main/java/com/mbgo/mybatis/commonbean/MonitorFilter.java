package com.mbgo.mybatis.commonbean;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 监控请求执行时间
 * 
 * @author
 * 
 */
public class MonitorFilter implements Filter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MonitorFilter.class);

	/**
	 * 初始化参数名称
	 */
	private final static String MONITOR = "monitor";
	private final static String TRACE_PARAMS = "traceParams";
	private final static String OPEN = "true";

	/**
	 * 是否开启监控 true:开启
	 */
	private boolean isMonitor = false;
	/**
	 * 是否打印请求参数，true:打印
	 */
	private boolean isTraceParams = false;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		StringBuilder buffer = new StringBuilder();
		if (isMonitor) {
			long beginTime = System.currentTimeMillis(); // 开始时间
			String temp = ((HttpServletRequest) request).getRequestURI();
			
			temp = temp.substring(temp.indexOf("/", 2) + 1);
			
			buffer.append(temp);

			if (isTraceParams) {
				buffer.append(" params:").append(paramsToString(request));
			}

			// 执行filter
			doFilter(request, response, filterChain, buffer);

			// 记录执行耗时等信息
			if (logger.isInfoEnabled()) {
				logger.info("{}, cost={}", temp, System.currentTimeMillis() - beginTime);
			}
		} else {
			doFilter(request, response, filterChain, buffer);
		}
	}

	/**
	 * 执行filter
	 * 
	 * @param request
	 * @param response
	 * @param filterChain
	 * @param buffer
	 * @throws IOException
	 * @throws ServletException
	 */
	private void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain, StringBuilder buffer) throws IOException,
			ServletException {
		try {
			filterChain.doFilter(request, response);

		} catch (IOException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (ServletException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		isMonitor = OPEN.equalsIgnoreCase(config.getInitParameter(MONITOR));
		isTraceParams = OPEN.equalsIgnoreCase(config
				.getInitParameter(TRACE_PARAMS));
	}

	@Override
	public void destroy() {

	}

	/**
	 * 将请求参数转化为字符串
	 * 
	 * @param request
	 * @return
	 */
	private String paramsToString(ServletRequest request) {
		if (request == null) {
			return "";
		}

		StringBuilder buffer = new StringBuilder();
		@SuppressWarnings("rawtypes")
		Enumeration names = request.getParameterNames();
		if (names != null) {
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				buffer.append(name + " = " + request.getParameter(name) + "; ");
			}
		}

		return buffer.toString();
	}

}
