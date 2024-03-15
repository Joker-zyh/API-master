package com.hengapi.hengapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestUtil;

import java.util.Map;

public class SignUtils {

    public static String getSign(Map<String,String> map, String secretKey){
        String md5Hex1 = DigestUtil.md5Hex(map.toString() + secretKey);
        return md5Hex1;
    }

    public static String getSignObject(Map<String,Object> map, String secretKey){
        String md5Hex1 = DigestUtil.md5Hex(map.toString() + secretKey);
        return md5Hex1;
    }
}
