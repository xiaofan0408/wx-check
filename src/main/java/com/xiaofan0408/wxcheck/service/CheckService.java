package com.xiaofan0408.wxcheck.service;

import com.xiaofan0408.wxcheck.common.exception.ServiceException;
import com.xiaofan0408.wxcheck.component.model.CheckResult;
import com.xiaofan0408.wxcheck.component.QQCheckComponent;
import com.xiaofan0408.wxcheck.component.WxCheckComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import reactor.core.publisher.MonoSink;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

@Service
public class CheckService {

    @Autowired
    private WxCheckComponent wxCheckComponent;

    @Autowired
    private QQCheckComponent qqCheckComponent;



    public Mono<CheckResult> checkWxDomain(String url) {
        if (url == null || url.isEmpty()) {
            return Mono.error(new ServiceException(5001,"url不能为空"));
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            return Mono.error(new ServiceException(5002,"url需要以http://或者https://开头"));
        }
        return wxCheckComponent.checkUrlOkHttp(url)
                .publishOn(Schedulers.boundedElastic())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<CheckResult> checkQQDomain(String url){
        if (url == null || url.isEmpty()) {
            return Mono.error(new ServiceException(5001,"url不能为空"));
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            return Mono.error(new ServiceException(5002,"url需要以http://或者https://开头"));
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
}