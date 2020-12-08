package com.xiaofan0408.wxcheck.controller;

import com.xiaofan0408.wxcheck.component.model.CheckResult;
import com.xiaofan0408.wxcheck.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CheckController {

    @Autowired
    private CheckService checkService;

    @GetMapping(value = "check",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<CheckResult> check2(@RequestParam("url")String url){
        return checkService.checkWxDomain(url);
    }

    @GetMapping(value = "qqCheck", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<CheckResult> qqCheck(@RequestParam("url")String url){
        return checkService.checkQQDomain(url);
    }
}
