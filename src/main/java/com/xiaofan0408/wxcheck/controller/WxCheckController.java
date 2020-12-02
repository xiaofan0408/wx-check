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
    public CheckResult check(@RequestParam("url")String url) throws Exception {
        return wxCheckService.checkDomain(url);
    }

    @GetMapping("check2")
    public CheckResult check2(@RequestParam("url")String url) throws Exception {
        return wxCheckService.checkDomain2(url);
    }
}
