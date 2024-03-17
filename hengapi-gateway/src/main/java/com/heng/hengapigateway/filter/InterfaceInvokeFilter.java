package com.heng.hengapigateway.filter;

import cn.hutool.json.JSONUtil;
import com.heng.hengapicommon.model.entity.InterfaceInfo;
import com.heng.hengapicommon.model.entity.User;
import com.heng.hengapicommon.model.entity.UserInterfaceInfo;
import com.heng.hengapicommon.service.InnerInterfaceInfoService;
import com.heng.hengapicommon.service.InnerUserInterfaceInfoService;
import com.heng.hengapicommon.service.InnerUserService;
import com.hengapi.hengapiclientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Component
public class InterfaceInvokeFilter implements GatewayFilter, Ordered {
    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;


    private static final String IP_HOST = "http://localhost:8123";


    /**
     * 全局过滤
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求表示：" + request.getId());
        String url = request.getPath().value();
        String method = request.getMethodValue();
        log.info("请求路径：" + url);
        log.info("请求方法：" + method);
        String hostString = request.getRemoteAddress().getHostString();
        log.info("请求参数：" + hostString);
        log.info("请求来源地址：" + request.getRemoteAddress());


        //2.黑白名单
        ServerHttpResponse response = exchange.getResponse();
        /*if (!IP_WHITE_LIST.contains(hostString)){
            return handleNoAuth(response);
        }*/


        //3。用户鉴权（ak，sk）
        //获取请求头内容
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");

        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        }catch (Exception e){
            log.error("getInvokeUser error");
        }
        if (invokeUser == null){
            log.error("用户不存在。");
            return handleNoAuth(response);
        }

        //检查时间戳与当前时间的差距
        if ((System.currentTimeMillis()/1000) - Integer.parseInt(timestamp) > 5*60){
            return handleNoAuth(response);
        }

        //拼接sign
        Map<String,String> map = new HashMap<>();
        map.put("accessKey",accessKey);
        User bodyUser = new User();
        bodyUser.setUserName(invokeUser.getUserName());
        map.put("body", JSONUtil.toJsonStr(bodyUser));
        map.put("timestamp",timestamp);
        String findSign = SignUtils.getSign(map, invokeUser.getSecretKey());
        if (!findSign.equals(sign)){
            log.error("签名校验失败。");
            return handleNoAuth(response);
        }


        //4.请求的接口是否存在
        //从数据库中查询接口是否存在（接口路径，请求方等是否匹配）
        InterfaceInfo interfaceInfo = null;
        try{
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(IP_HOST + url,method);
        }catch (Exception e){
            log.error("远程调用获取接口信息失败。");
        }

        if (interfaceInfo == null){
            log.error("接口不存在。");
            return handleNoAuth(response);
        }



        //5.判断用户对该接口的调用次数是否大于0
        //获取用户接口信息
        UserInterfaceInfo userInterfaceInfoById = null;
        try{
            userInterfaceInfoById = innerUserInterfaceInfoService.getUserInterfaceInfoById(
                    invokeUser.getId(), interfaceInfo.getId());
        }catch (Exception e){
            log.error("远程调用获取用户接口信息失败。");
        }

        if (userInterfaceInfoById == null){
            log.error("用户接口信息不存在。");
            return handleNoAuth(response);
        }

        //判断接口剩余次数是否大于0
        if (userInterfaceInfoById.getLeftNum() <= 0){
            log.error("接口剩余次数不足。");
            return handleNoAuth(response);
        }


        //6.请求转发，调用模拟接口
        //调用并响应日志
        return handleResponse(exchange,chain,invokeUser.getId(), interfaceInfo.getId());
    }

    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,
                                      long userId, long interfaceId){
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode != HttpStatus.OK) {
                return chain.filter(exchange);//降级处理返回数据
            }
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

                            // 合并多个流集合，解决返回体分段传输
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer buff = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[buff.readableByteCount()];
                            buff.read(content);
                            DataBufferUtils.release(buff);//释放掉内存

                            //7.响应日志
                            String joinData = new String(content);
                            log.info("响应：" + joinData);

                            //8.调用成功，调用次数+1
                            try{
                                innerUserInterfaceInfoService.invokeCount(interfaceId,userId);
                            }catch (Exception e){
                                log.error("invokeCount error");
                            }

                            return bufferFactory.wrap(content);
                        }));
                    } else {
                        log.error("<-- {} 响应code异常", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            return chain.filter(exchange.mutate().response(decoratedResponse).build());

        } catch (Exception e) {
            log.error("网关处理响应异常\n" + e);
            return chain.filter(exchange);
        }
    }


    /**
     * 处理无权限调用异常
     * @param response
     * @return
     */
    private Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -2;
    }
}

