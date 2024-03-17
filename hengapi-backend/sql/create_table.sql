use hengapi;

-- 接口计费
create table if not exists hengapi.`interface_charging`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `interfaceId` bigint not null comment '接口id',
    `charging` double not null comment '用户名',
    `availablePieces` varchar(256) not null comment '接口剩余可调用次数',
    `userId` bigint not null comment '创建人',
    `create_time` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `update_time` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `is_deleted` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '接口计费';

insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (1, 8, 30596.877, '20', 20079);
insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (2, 4670863, 80079.445, '20', 713110876);
insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (3, 3687960188, 91537.3, '20', 821556);
insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (4, 6277451794, 23509.95, '20', 6499035);
insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (5, 406, 70622.195, '20', 24);
insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (6, 3952, 97793.39, '20', 67);
insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (7, 582, 11516.732, '20', 3961756229);
insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (8, 45255, 41737.28, '20', 18);
insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (9, 718526215, 69846.33, '20', 3015);
insert into hengapi.`interface_charging` (`id`, `interfaceId`, `charging`, `availablePieces`, `userId`) values (10, 681163, 46777.945, '20', 1476818674);