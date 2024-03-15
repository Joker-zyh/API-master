package com.heng.hengapigateway.filter;


import com.google.common.util.concurrent.RateLimiter;
import com.heng.hengapicommon.common.ErrorCode;
import com.heng.hengapicommon.common.JwtUtils;
import com.heng.hengapicommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class LoginGlobalFilter implements GlobalFilter, Ordered {
    @Resource
    private RateLimiter rateLimiter;

    public static final List<String> NOT_LOGIN_PATH = Arrays.asList(
            "/api/user/login", "/api/user/loginBySms", "/api/user/register","/api/user/email/register", "/api/user/smsCaptcha",
            "/api/user/getCaptcha", "/api/interface/**","/api/third/alipay/**","/api/interfaceInfo/sdk");


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();


        //1. 限流过滤
        if (!rateLimiter.tryAcquire()){
            log.error("请求频繁");
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }

        //2. 登陆过滤，路径判断
        String path = request.getPath().toString();
        List<Boolean> collect = NOT_LOGIN_PATH.stream().map(notLoginPath -> {
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            return antPathMatcher.match(notLoginPath, path);
        }).collect(Collectors.toList());

        //是否需要登录,有true为不需要登录，放行
        if (collect.contains(true)){
            return chain.filter(exchange);
        }

        //3. 需要登陆，获取Cookie，检查是否有token
        String cookie = headers.getFirst("Cookie");
        //无cookie，没有登陆
        if (StringUtils.isBlank(cookie)){
            log.error("未登录");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isTokenValid = false;
        String[] split = cookie.split(";");
        for(String s : split){
            String[] keyAndVal = s.split("=");
            String cookieName = keyAndVal[0];
            if (cookieName.trim().equals("token")){
                isTokenValid = JwtUtils.checkToken(keyAndVal[1]);
            }
        }
        if (isTokenValid){
            log.error("未登录");
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
