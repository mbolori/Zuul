package com.example.zuul.filter;

import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 Pre Filter Zuul filter: It is ran before PRE and ROUTING filters. 
 
 * It uses thread-local container RequestContext to access data about  original request:
 *    request headers, remote host, requested url.
 *
 */
@Component
public class PreFilterZuul extends ZuulFilter {

	private final Logger log = LoggerFactory.getLogger(PreFilterZuul.class);

	public static final String RequestId = "RequestId";
		
	private AtomicLong counter = new AtomicLong(1);
	@Override
	public Object run() {
		Long requestId = counter.getAndIncrement();
		MDC.put(RequestId, requestId );
		
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.put(RequestId, requestId);
		HttpServletRequest request = ctx.getRequest();
		log.info("============== [PreFilterZuul BEGIN] =================");
		
		StringBuilder headerString = new StringBuilder();
		for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
			String headerName = headerNames.nextElement();
			headerString.append(headerName).append("=").append(request.getHeader(headerName)).append(",");
		}
		log.info("HEADERS= [" + headerString + "]");	
		log.info("REMOTE_HOST=" + request.getRemoteHost() + ":" + request.getRemotePort());
		log.info("REQUESTED_URL=" + request.getRequestURL());
		log.info("============== [PreFilterZuul END] =================");
		
		return null;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public int filterOrder() {
		return FilterConstants.DEBUG_FILTER_ORDER;
	}

	@Override
	public String filterType() {
		return FilterConstants.PRE_TYPE;
	}
	
}
