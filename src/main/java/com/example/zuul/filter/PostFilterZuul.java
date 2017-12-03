package com.example.zuul.filter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.ribbon.apache.RibbonApacheHttpResponse;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class PostFilterZuul extends ZuulFilter{

	private final Logger log = LoggerFactory.getLogger(PostFilterZuul.class);
	
	private static final String customHeader = "X-Zuul-Handled";
	
	private static final String OK = "OK";
	
	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return ctx.get(FilterConstants.SERVICE_ID_KEY) !=null ? true : false;
	}

	@Override
	public Object run() {
		log.info("============== [PostFilterZuul BEGIN] =================");
		
		RequestContext ctx = RequestContext.getCurrentContext();
		//HttpServletResponse response = ctx.getResponse();
		RibbonApacheHttpResponse ribbonResponse =  (RibbonApacheHttpResponse) ctx.get("ribbonResponse");
		String serviceId = (String) ctx.get(FilterConstants.SERVICE_ID_KEY);
		String towardsUri = ribbonResponse.getRequestedURI().toString();
		log.info("FORWARDED_TOWARDS=" + serviceId + ", URL=" + towardsUri);
		log.info("ZUUL_ADDED_HEADERS=" + ctx.get("zuulRequestHeaders"));
		log.info("RESPONSE_RECEIVED="+ ribbonResponse.getStatusLine() + ",HEADERS=" + ribbonResponse.getHeaders());
		
		HttpServletResponse servletResponse = ctx.getResponse();
		servletResponse.addHeader(customHeader, OK);
		log.info("ADDED CUSTOM HEADER. NAME=" + customHeader + "=" + OK);
		log.info("============== [PostFilterZuul END] =================");
		return null;
	}

	@Override
	public String filterType() {
		return FilterConstants.POST_TYPE;
	}

	@Override
	public int filterOrder() {
		return FilterConstants.RIBBON_ROUTING_FILTER_ORDER + 1;
	}

}
