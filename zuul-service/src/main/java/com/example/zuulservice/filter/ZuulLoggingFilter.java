package com.example.zuulservice.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component //Bean으로 등록
public class ZuulLoggingFilter extends ZuulFilter {
    //로그를 출력해 줄 필터
    //Logger logger = LoggerFactory.getLogger(ZuulLoggingFilter.class);
    @Override
    public Object run() throws ZuulException {
        log.info("************ printing logs : ");
        RequestContext ctx = RequestContext.getCurrentContext(); //Context를 얻어온다
        HttpServletRequest request = ctx.getRequest();
        log.info("************ " + request.getRequestURI());
        return null;
    }

    @Override
    public String filterType() {
        //사전 필터인지, 사후 필터인지
        return "pre";
    }

    @Override
    public int filterOrder() {
        //필터 순서
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        //필터 사용여부
        return true;
    }
}
