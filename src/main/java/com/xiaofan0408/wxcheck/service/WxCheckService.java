package com.xiaofan0408.wxcheck.service;

import com.xiaofan0408.wxcheck.component.CheckResult;
import com.xiaofan0408.wxcheck.component.WxCheckComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import reactor.core.publisher.MonoSink;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

@Service
public class WxCheckService {

    @Autowired
    private WxCheckComponent wxCheckComponent;

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
        }).publishOn(Schedulers.newBoundedElastic(128,1024,"check"));
    }

}