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

    public CheckResult checkDomain(String url) throws Exception {
        if (url == null && url.isEmpty()) {
            throw new Exception("url 不能为空");
        }
        if (!(url.startsWith("http") || url.startsWith("https"))) {
            throw new Exception("url需要带上http或者https");
        }
        return wxCheckComponent.checkUrl(url);
    }

    public CheckResult checkDomain2(String url) throws Exception {
        return wxCheckComponent.checkUrlOkHttp(url);
    }
}