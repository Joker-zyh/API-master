package com.heng.hengapicommon.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮箱 && 手机短信消息对象
 */
@Data
public class SmsMessage implements Serializable {

//    /**
//     * 手机号码
//     */
//    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 短信
     */
    private String code;

    /**
     * 短信类型，登陆验证码，注册验证码
     */
    private String codeType;

    public SmsMessage(String email, String code, String codeType) {
        this.email = email;
        this.code = code;
        this.codeType = codeType;
    }

    public SmsMessage() {
    }
}
