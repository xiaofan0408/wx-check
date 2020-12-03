package com.xiaofan0408.wxcheck.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaofan0408.wxcheck.component.model.CheckResult;
import com.xiaofan0408.wxcheck.component.model.QQResult;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class QQCheckComponent {

    private static final String CHECK_URL = "https://cgi.urlsec.qq.com/index.php?m=check&a=check&url=%s";

    private OkHttpClient okHttpClient;

    private String DEFAULT_HEADER = "Referer";

    private String DEFAULT_HEADER_VALUE = "https://guanjia.qq.com";

    @PostConstruct
    public void init() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1024);
        dispatcher.setMaxRequestsPerHost(1024);
        okHttpClient = new OkHttpClient().newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2,TimeUnit.SECONDS)
                .build();
    }

    //whitetype 1：安全性未知 2：危险网站 3：安全网站
    public CheckResult check(String url) throws Exception{
        CheckResult checkResult = new CheckResult();

        String checkUrl = String.format(CHECK_URL,url);
        Request request = new Request.Builder()
                .url(checkUrl)
                .get()
                .header(DEFAULT_HEADER,DEFAULT_HEADER_VALUE)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String body = response.body().string();
        String subBody = body.substring(1,body.length() -1);
        JSONObject jsonObject = JSON.parseObject(subBody);
        JSONObject results = getDataResult(jsonObject);
        if (results != null) {
            QQResult qqResult = results.toJavaObject(QQResult.class);
            JSONObject data = new JSONObject();
            if (qqResult.getWhitetype() == 1 || qqResult.getWhitetype() == 3) {
                checkResult.setResult(1);
                data.put("icpSerial",qqResult.getICPSerial());
                data.put("orgnization",qqResult.getOrgnization());
                data.put("isDomainICPOk",qqResult.getIsDomainICPOk());
                checkResult.setDetail(data);
            } else {
                checkResult.setResult(2);
                data.put("title",qqResult.getWordingTitle());
                data.put("desc",qqResult.getWording());
                checkResult.setDetail(data);
            }
        } else {
            throw new Exception("检测查询失败");
        }
        return checkResult;
    }

    private JSONObject getDataResult(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        if (null != data) {
            return data.getJSONObject("results");
        }
        return null;
    }

}
