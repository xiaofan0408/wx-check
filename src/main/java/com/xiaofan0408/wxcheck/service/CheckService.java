package com.xiaofan0408.wxcheck.service;

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

    public Mono<CheckResult> checkDomain(String url){
        return Mono.create(new Consumer<MonoSink<CheckResult>>() {
            @Override
            public void accept(MonoSink<CheckResult> sink) {
                try {
                    if (url == null && url.isEmpty()) {
                       sink.error(new Exception("url 不能为空"));
                    }
                    if (!(url.startsWith("http") || url.startsWith("https"))) {
                        sink.error(new Exception("url需要带上http或者https"));
                    }
                    sink.success(wxCheckComponent.checkUrl(url));
                }catch (Exception e){
                    sink.error(e);
                }
            }
        }) .publishOn(Schedulers.boundedElastic())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<CheckResult> checkDomain2(String url) {
        return wxCheckComponent.checkUrlOkHttp(url)
                .publishOn(Schedulers.boundedElastic())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<CheckResult> checkDomain3(String url) {
        return Mono.create(new Consumer<MonoSink<CheckResult>>() {
            @Override
            public void accept(MonoSink<CheckResult> sink) {
                try {
                    Thread.sleep(100);
                    sink.success(new CheckResult());
                }catch (Exception e){
                    sink.error(e);
                }
            }
        }).subscribeOn(Schedulers.newBoundedElastic(2048,8192,"test"));
    }

    public Mono<CheckResult> checkQQDomain(String url){
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