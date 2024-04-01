package com.heng.hengapithirdparty.utils;



import com.heng.hengapicommon.model.entity.SmsMessage;
import com.heng.hengapithirdparty.config.QQEmailConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.heng.hengapicommon.constant.RedisConstant.LOGINCODEPRE;
import static com.heng.hengapicommon.constant.RedisConstant.REGISTERCODEPRE;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageOperation {

    private SimpleEmail mail=new SimpleEmail();
    public  void sendMessage(SmsMessage smsMessage, RedisTemplate<String,String> redisTemplate)  {
        try {
            // 设置邮箱服务器信息
            mail.setSslSmtpPort(QQEmailConfig.PORT);
            mail.setHostName(QQEmailConfig.HOST);
            // 设置密码验证器
            mail.setAuthentication(QQEmailConfig.EMAIL, QQEmailConfig.PASSWORD);
            // 设置邮件发送者
            mail.setFrom(QQEmailConfig.EMAIL);
            // 设置邮件接收者
            mail.addTo(smsMessage.getEmail());
            // 设置邮件编码
            mail.setCharset("UTF-8");
            // 设置邮件主题
            mail.setSubject("Heng API");

            String code = RandomUtil.getFourBitRandom();
            //设置数据的5分钟有效期限
            if(smsMessage.getCodeType().equals("register")){
                redisTemplate.opsForValue().set(REGISTERCODEPRE+smsMessage.getEmail(),code,5,TimeUnit.MINUTES);
            }else {
                redisTemplate.opsForValue().set(LOGINCODEPRE+smsMessage.getEmail(),code,5,TimeUnit.MINUTES);
            }

            // 设置邮件内容
            mail.setMsg("您的注册 or 登录 验证码为："+code+",验证码5分钟内有效!!!"+"[Heng API]");
            // 设置邮件发送时间
            mail.setSentDate(new Date());
            // 发送邮件
            mail.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}
