package com.xiaofan0408.wxcheck.component;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class WxCheckComponent {

    private static final String CHECK_URL = "http://mp.weixinbridge.com/mp/wapredirect?url=%s&action=appmsg_redirect&uin=&biz=MzUxMTMxODc2MQ==&mid=100000007&idx=1&type=1&scene=0";

    private Pattern pattern = Pattern.compile("var cgiData =(.*);");

    public CheckResult checkUrl(String url) throws Exception {
        CheckResult checkResult = new CheckResult();
        try {
            String checkUrl = String.format(CHECK_URL,url);
            Connection connection = Jsoup.connect(checkUrl);
            connection.followRedirects(true);
            connection.timeout(5000);
            Connection.Response response = connection.execute();
            String sourceUrl = getDomain(url);
            String resultUrl = response.url().getHost();
            if (resultUrl.contains("weixin110.qq.com")) {
                String body = response.body();
                String json = getErrorJson(body);
                JSONObject jsonObject = JSON.parseObject(json);
                JSONObject detail = new JSONObject();
                detail.put("type",jsonObject.get("type"));
                detail.put("title",jsonObject.get("title"));
                detail.put("desc", jsonObject.get("desc"));
                checkResult.setResult(2);
                checkResult.setDetail(detail);
            } else {
                checkResult.setResult(1);
            }
            return checkResult;
        }catch (Exception e){
            if ((e instanceof SocketTimeoutException) || (e instanceof UnknownHostException)) {
                checkResult.setResult(1);
                return checkResult;
            }else {
                throw new Exception(e);
            }
        }
    }

    private String getDomain(String url) throws MalformedURLException {
        if (url != null) {
            URL u = new URL(url);
            return u.getHost();
        }
        return "";
    }

    private String getErrorJson(String body){
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

        WxCheckComponent wxCheckComponent = new WxCheckComponent();
        CheckResult checkResult = wxCheckComponent.checkUrl("http://69296n.cn");
        System.out.println(checkResult.getResult());

    }
}
