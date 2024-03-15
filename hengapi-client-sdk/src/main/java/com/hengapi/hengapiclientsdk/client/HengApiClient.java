package com.hengapi.hengapiclientsdk.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.hengapi.hengapiclientsdk.model.User;
import com.hengapi.hengapiclientsdk.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

public class HengApiClient {
    private final String accessKey;
    private final String secretKey;
    public static final String GATEWAY_HOST = "http://localhost:8090";

    public HengApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    //通过GET请求获取信息
    public String getNameByGET(String name){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        paramMap.put("accessKey", accessKey);
        paramMap.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
        paramMap.put("sign", SignUtils.getSignObject(paramMap,secretKey));

        String result= HttpUtil.get(GATEWAY_HOST + "/api/name/get/", paramMap);
        System.out.println(result);
        return result;
    }

    //通过POST请求获取信息
    public String getNameByPOST(String name){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpRequest.post(GATEWAY_HOST + "/api/name/post/")
                .addHeaders(getHeaders(name))
                .form(paramMap)
                .execute().body();
        System.out.println(result);
        return result;
    }

    //通过POST方法向服务器发送User对象，获取返回的结果
    public String getUsernameByPOST(User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user/")
                .addHeaders(getHeaders(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }

    //请求头
    private Map<String,String> getHeaders(String body){
        Map<String,String> map = new HashMap<>();
        map.put("accessKey",accessKey);
        //map.put("secretKey",secretKey);
        //知识为了拼接参数
        map.put("body",body);
        map.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000));
        map.put("sign", SignUtils.getSign(map,secretKey));
        return map;
    }
}
