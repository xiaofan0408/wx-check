package com.xiaofan0408.wxcheck.controller;

import com.xiaofan0408.wxcheck.component.CheckResult;
import com.xiaofan0408.wxcheck.service.WxCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class WxCheckController {

    @Autowired
    private WxCheckService wxCheckService;

    @GetMapping("check")
    public Mono<CheckResult> check(@RequestParam("url")String url){
        return wxCheckService.checkDomain(url);
    }
}
