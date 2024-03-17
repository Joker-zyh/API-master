use hengapi;

-- 订单表
create table if not exists hengapi.`order`
(
    `id` bigint not null auto_increment comment '订单编号' primary key,
    `orderSn` varchar(256) not null comment '订单编号',
    `interfaceId` bigint not null comment '接口id',
    `userId` bigint not null comment '用户id',
    `charging` double not null comment '单价',
    `count` int not null comment '购买数量',
    `totalAmount` double not null comment '订单应付价格',
    `state` tinyint default 0 not null comment '支付状态 (0 未支付  1 已支付  2 已取消/超时)',
    `pay_time` datetime null comment '支付时间',
    `create_time` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `update_time` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `is_deleted` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '订单表';

insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (1, '3Vyv', 14, 4, 902641612, 118801, 80339);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (2, 'LUGW', 1, 2, 865487, 89, 95563354);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (3, 'UZB', 20, 4, 7609011, 5583505, 2066779581);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (4, 'IcHg', 5, 2, 5, 57381623, 57949);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (5, 'vP', 9, 4, 36542297, 4475428, 5);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (6, 'CO41', 8, 3, 7860638, 1, 265059);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (7, '3MUz', 7, 3, 2592805, 5625486, 791194375);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (8, '71m', 2, 2, 386477205, 21219, 53);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (9, 'tp06', 2, 2, 9612306, 603257, 6733351);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (10, 'CF', 13, 3, 527964043, 608298468, 19696082);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (11, 'y3drb', 1, 2, 683, 81, 7956939);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (12, 'COqFH', 8, 5, 740322, 43113, 989343982);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (13, 'dF', 20, 4, 10, 741295795, 63);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (14, 'aNLi', 1, 2, 18422, 9, 19);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (15, 'kF', 1, 3, 52109, 509133, 4707);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (16, 'eU2', 9, 4, 90, 5, 70);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (17, 'qKSe', 15, 4, 672631, 796, 6965);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (18, '4I', 1, 1, 76747, 619, 80484);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (19, 'rXBG', 2, 2, 618, 49510, 138680502);
insert into hengapi.`order` (`id`, `orderSn`, `interfaceId`, `userId`, `charging`, `count`, `totalAmount`) values (20, 'Z2', 8, 3, 30002, 269, 138250796);