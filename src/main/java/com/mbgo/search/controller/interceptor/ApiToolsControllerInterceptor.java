/*
* 2014-12-9 上午10:58:26
* 吴健 HQ01U8435
*/

package com.mbgo.search.controller.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ApiToolsControllerInterceptor extends HandlerInterceptorAdapter {
	private long beginTime = 0;
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String method = request.getMethod();
		if(method.equalsIgnoreCase("get")) {
			output(response, "必须以POST方式提交！");
			return false;
		}

		String password = request.getParameter("password");
		if(StringUtils.isBlank(password) || !password.equalsIgnoreCase("mbSearch2014")) {
			output(response, "密码有误！");
			return false;
		}
		beginTime = System.currentTimeMillis();
		return true;
	}
	
//	@Override
//	public void postHandle(HttpServletRequest request,
//			HttpServletResponse response, Object handler,
//			ModelAndView modelAndView) throws Exception {
//		super.postHandle(request, response, handler, modelAndView);
//	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long end = System.currentTimeMillis();
		String temp = getCost(beginTime, end);
		if(end - beginTime < 300) {
			output(response, "重建索引失败，已有另一线程在执行此操作！");
		} else {
			output(response, "重建索引成功, 耗时 " + temp + ".");
		}
	}
	
	public void output(HttpServletResponse response, String outputStr) {
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(outputStr);
		} catch (IOException e) {
			
		}
	}
	
	public String getCost(long begin, long end) {

		String rs = "";
		try {
			Interval interval = new Interval(begin, end);
			Period p = interval.toPeriod();
			
			int day = p.getDays(), hour = p.getHours(), minuts = p.getMinutes(), second = p.getSeconds(), millis = p.getMillis();
			
			rs += append(day, rs, "天");
			rs += append(hour, rs, "小时");
			rs += append(minuts, rs, "分钟");
			rs += append(second, rs, "秒");
			rs += append(millis, rs, "毫秒");
		} catch (Exception e) {
		}
		
		return rs.trim();
	}
	
	public String append(int value, String rs, String dw) {
		if(value < 1 && StringUtils.isBlank(rs)) {
			return "";
		} else {
			return " " + value + "" + dw;
		}
	}
	
}
