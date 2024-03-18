use hengapi;

-- 支付信息表
create table if not exists hengapi.`alipay_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `orderNumber` varchar(256) not null comment '订单ID',
    `subject` varchar(256) not null comment '交易名称',
    `totalAmount` double not null comment '交易金额',
    `buyerPayAmount` double not null comment '买家付款金额',
    `buyerId` varchar(256) not null comment '买家在支付宝的唯一id',
    `tradeNo` varchar(256) not null comment '支付宝交易凭证号',
    `tradeStatus` varchar(256) not null comment '交易状态',
    `gmtPayment` date not null comment '买家付款时间'
) comment '支付信息表';

insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (1, '李昊天', '邹语堂', 7575213, 12314, '于正豪', '覃潇然', '董鸿涛', '2022-11-20 02:03:23');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (2, '蒋明', '郭明杰', 95, 185, '唐雨泽', '雷梓晨', '丁鑫鹏', '2022-03-23 17:31:22');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (3, '潘驰', '洪伟泽', 2284, 9340436060, '程天宇', '吴熠彤', '萧智辉', '2022-06-30 05:11:14');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (4, '姚志强', '丁鹏飞', 3514994, 5593973751, '黎思源', '彭鹏煊', '叶浩宇', '2022-03-14 13:45:26');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (5, '邹晟睿', '黎浩轩', 89, 284004, '钟彬', '许昊焱', '程鸿煊', '2022-01-10 01:05:56');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (6, '程擎苍', '吕瑞霖', 932500705, 2396620022, '魏雨泽', '龙梓晨', '夏聪健', '2022-08-16 00:02:01');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (7, '马文', '蒋绍辉', 5116526, 8404, '蔡熠彤', '范立辉', '宋浩轩', '2022-02-07 10:54:50');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (8, '吕越泽', '黄炎彬', 133579, 4, '沈智辉', '秦明轩', '汪文博', '2022-04-24 05:07:58');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (9, '韩炎彬', '龚立果', 341280, 456, '罗远航', '周瑾瑜', '姜鹏', '2022-06-18 19:31:52');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (10, '周浩轩', '宋彬', 8977948, 37208830, '任擎宇', '范立辉', '余浩宇', '2022-11-08 00:26:32');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (11, '陶博超', '谢峻熙', 25680, 90251, '邵鸿煊', '吕明轩', '叶煜城', '2022-05-15 03:26:27');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (12, '石涛', '覃昊焱', 70813346, 835633, '邵鑫鹏', '董嘉懿', '杨擎苍', '2022-08-20 07:02:46');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (13, '朱弘文', '贾苑博', 38624, 98173, '朱俊驰', '韩立轩', '史鑫磊', '2022-07-07 16:45:07');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (14, '周君浩', '田文', 98512786, 2220956655, '曹晓博', '毛金鑫', '袁思远', '2022-02-12 23:03:29');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (15, '邱锦程', '郑晓啸', 501, 21, '周烨磊', '方涛', '田明辉', '2022-12-22 14:46:16');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (16, '高智渊', '熊健柏', 243606584, 35609, '范鸿煊', '龙展鹏', '周明轩', '2022-04-18 12:58:14');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (17, '罗天磊', '杨俊驰', 998162, 804528481, '黄钰轩', '黎荣轩', '丁弘文', '2022-05-16 02:40:50');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (18, '姚风华', '郑文轩', 852325, 449665627, '王思聪', '王嘉懿', '苏天翊', '2022-03-06 12:30:20');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (19, '顾伟泽', '高子涵', 563958153, 48460463, '田文轩', '傅钰轩', '许鸿涛', '2022-10-21 20:14:44');
insert into hengapi.`alipay_info` (`id`, `orderNumber`, `subject`, `totalAmount`, `buyerPayAmount`, `buyerId`, `tradeNo`, `tradeStatus`, `gmtPayment`) values (20, '陶天宇', '石天翊', 81985675, 870829060, '苏思源', '汪风华', '程雪松', '2022-07-24 06:06:56');