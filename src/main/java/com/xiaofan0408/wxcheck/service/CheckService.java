package com.xiaofan0408.wxcheck.service;

import com.xiaofan0408.wxcheck.common.exception.ServiceException;
import com.xiaofan0408.wxcheck.component.model.CheckResult;
import com.xiaofan0408.wxcheck.component.QQCheckComponent;
import com.xiaofan0408.wxcheck.component.WxCheckComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import reactor.core.publisher.MonoSink;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

@Slf4j
@Service
public class CheckService {

    @Autowired
    private WxCheckComponent wxCheckComponent;

    @Autowired
    private QQCheckComponent qqCheckComponent;



    public Mono<CheckResult> checkWxDomain(String url,ServerWebExchange serverWebExchange) {
        log.info("client ip:{},wx check url:{}",getIp(serverWebExchange),url);
        if (url == null || url.isEmpty()) {
            throw new ServiceException(5001,"url不能为空");
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            throw new ServiceException(5002,"url需要以http://或者https://开头");
        }
        return wxCheckComponent.checkUrlOkHttp(url,true)
                .publishOn(Schedulers.boundedElastic())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<CheckResult> checkQQDomain(String url,ServerWebExchange serverWebExchange){
        log.info("client ip:{},qq check url:{}",getIp(serverWebExchange),url);
        if (url == null || url.isEmpty()) {
            throw new ServiceException(5001,"url不能为空");
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            throw new ServiceException(5002,"url需要以http://或者https://开头");
        }
        return Mono.create(new Consumer<MonoSink<CheckResult>>() {
            @Override
            public void accept(MonoSink<CheckResult> sink) {
                try {
                    CheckResult checkResult = qqCheckComponent.check(url);
                    sink.success(checkResult);
                } catch (Exception e){
                    sink.error(e);
                }
            }
        });
    }

    public String getIp(ServerWebExchange serverWebExchange){
        ServerHttpRequest request = serverWebExchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }

        return ip.replaceAll(":", ".");
    }

    public Mono<CheckResult> checkWxDomainNoDetail(String url, ServerWebExchange serverWebExchange) {
        log.info("client ip:{},wx check url:{}",getIp(serverWebExchange),url);
        if (url == null || url.isEmpty()) {
            throw new ServiceException(5001,"url不能为空");
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            throw new ServiceException(5002,"url需要以http://或者https://开头");
        }
        return wxCheckComponent.checkUrlOkHttp(url,false)
                .publishOn(Schedulers.boundedElastic())
                .subscribeOn(Schedulers.boundedElastic());
    }
}