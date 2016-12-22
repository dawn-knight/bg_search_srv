/*
* 2014-11-5 上午10:33:57
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service.use4;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;


public class InitialContextListener extends ContextLoaderListener {
	@Override
	public void contextInitialized ( ServletContextEvent sce )
	{
		super.contextInitialized(sce);

		ApplicationContext ac = (WebApplicationContext) sce.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

		CacheService cacheService = ac.getBean("cacheService", CacheService.class);
		
		log.debug("************************ init data begin************************");
		cacheService.initAll();
		log.debug("************************ init data end************************");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
	private static Logger log =  LoggerFactory.getLogger(InitialContextListener.class);
}
