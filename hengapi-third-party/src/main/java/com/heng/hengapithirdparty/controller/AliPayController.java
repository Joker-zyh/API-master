package com.heng.hengapithirdparty.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.heng.hengapicommon.common.BaseResponse;
import com.heng.hengapicommon.common.ResultUtils;
import com.heng.hengapithirdparty.config.AliPayConfig;
import com.heng.hengapithirdparty.model.dto.AlipayRequest;
import com.heng.hengapithirdparty.model.entity.AlipayInfo;
import com.heng.hengapithirdparty.service.AlipayInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.heng.hengapicommon.constant.RabbitmqConstant.ORDER_EXCHANGE_NAME;
import static com.heng.hengapicommon.constant.RabbitmqConstant.ORDER_SUCCESS_EXCHANGE_ROUTING_KEY;
import static com.heng.hengapicommon.constant.RedisConstant.*;

@Slf4j
@RestController
@RequestMapping("/alipay")
public class AliPayController {

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private AlipayInfoService alipayInfoService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;


    @PostMapping("/payCode")
    public BaseResponse<String> payCode(@RequestBody AlipayRequest alipayRequest) throws AlipayApiException {

        String outTradeNo = alipayRequest.getTraceNo();
        String subject = alipayRequest.getSubject() ;
        double totalAmount = alipayRequest.getTotalAmount();

        AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getGatewayUrl(),
                aliPayConfig.getAppId(),
                aliPayConfig.getPrivateKey(),
                "json",aliPayConfig.getCharset(),
                aliPayConfig.getPublicKey(),aliPayConfig.getSignType());

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();

        request.setNotifyUrl(aliPayConfig.getNotifyUrl());

        request.setBizModel(model);
        model.setOutTradeNo(outTradeNo);
        model.setTotalAmount(String.valueOf(totalAmount));
        model.setSubject(subject);

        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        log.info("响应支付二维码详情："+response.getBody());

        String base64 = QrCodeUtil.generateAsBase64(response.getQrCode(), new QrConfig(300, 300), "png");

        return ResultUtils.success(base64);
    }


    /**
     * 支付成功回调,注意这里必须是POST接口
     * @param request
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/notify")
    public synchronized void payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }

            // 支付宝验签
            if (AlipaySignature.rsaCheckV1 (params, aliPayConfig.getPublicKey(), aliPayConfig.getCharset(), aliPayConfig.getSignType())) {
                //验证成功
                log.info("支付成功:{}",params);
                // 幂等性保证：判断该订单号是否被处理过，解决因为多次重复收到阿里的回调通知导致的订单重复处理的问题
                Object outTradeNo = stringRedisTemplate.opsForValue().get(ALIPAY_TRADE_SUCCESS_RECORD + params.get("out_trade_no"));
                if (null == outTradeNo ){
                    // 验签通过，将订单信息存入数据库
                    AlipayInfo alipayInfo = new AlipayInfo();
                    alipayInfo.setSubject(params.get("subject"));
                    alipayInfo.setTradeStatus(params.get("trade_status"));
                    alipayInfo.setTradeNo(params.get("tradeNo"));
                    alipayInfo.setOrderNumber(params.get("out_trade_no"));
                    alipayInfo.setTotalAmount(Double.valueOf(params.get("total_amount")));
                    alipayInfo.setBuyerId(params.get("buyer_id"));
                    alipayInfo.setGmtPayment(DateUtil.parse(params.get("gmt_payment")));
                    alipayInfo.setBuyerPayAmount(Double.valueOf(params.get("buyer_pay_amount")));
                    alipayInfoService.save(alipayInfo);

                    //记录处理成功的订单，实现订单幂等性
                    stringRedisTemplate.opsForValue().set(ALIPAY_TRADE_SUCCESS_RECORD +alipayInfo.getOrderNumber(),EXIST_KEY_VALUE,30, TimeUnit.MINUTES);

                    //给支付成功消息队列发送消息，修改数据库，完成整个订单功能
                    String tradeNo = params.get("out_trade_no");

                    //实现我方订单的幂等性
                    stringRedisTemplate.opsForValue().set(SEND_ORDER_PAY_SUCCESS_INFO + tradeNo,tradeNo);

                    rabbitTemplate.convertAndSend(ORDER_EXCHANGE_NAME,ORDER_SUCCESS_EXCHANGE_ROUTING_KEY,tradeNo,message -> {
                        MessageProperties messageProperties = message.getMessageProperties();
                        messageProperties.setMessageId(UUID.randomUUID().toString());
                        messageProperties.setContentEncoding("utf-8");
                        return message;
                    });
                    log.info("消息队列成功给订单服务发送支付成功消息，订单号为：" + tradeNo);
                    //orderPaySuccessMqUtils.sendOrderPaySuccess(params.get("out_trade_no"));
                }
            }
        }
    }
}
