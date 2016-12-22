package com.mbgo.search.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggingAspect {
	static private Logger log = LoggerFactory.getLogger(LoggingAspect.class);

	@Around("execution(* com.mbgo.search.core.service.ProductIndexService.rebuildProductIndex(..))")
	public void logAroundRebuilding(ProceedingJoinPoint joinPoint) throws Throwable {
		long startPoint = System.currentTimeMillis();
		log.debug("{} starts at {}", joinPoint.getSignature().getName(),
				startPoint);
		joinPoint.proceed();
		long endPoint = System.currentTimeMillis();
		log.debug("{} ends at {}", joinPoint.getSignature().getName(), endPoint);
		log.debug("{} ms 4 {} to execute", endPoint - startPoint, joinPoint
				.getSignature().getName());
	}
	
	@Around("execution(* com.mbgo.search.core.service.DataEtlService.getProductList(..))")
	public void logAroundGettGoods(ProceedingJoinPoint joinPoint) throws Throwable {
		long startPoint = System.currentTimeMillis();
		log.debug("{} starts at {}", joinPoint.getSignature().getName(),
				startPoint);
		joinPoint.proceed();
		long endPoint = System.currentTimeMillis();
		log.debug("{} ends at {}", joinPoint.getSignature().getName(), endPoint);
		log.debug("{} ms 4 {} to execute", endPoint - startPoint, joinPoint
				.getSignature().getName());
	}
}
	