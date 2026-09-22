--建表
CREATE TABLE `t_user` (
    `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
    `nick_name` varchar(35) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '头像',
    `true_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '真实姓名',
    `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男，1女',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB COMMENT='用户基础信息表';
--插入测试用户
INSERT INTO tllive.t_user (user_id, nick_name, avatar, true_name, sex, create_time, update_time) VALUES (1, 'roy', '/img/avatar.png', 'test', 1, '2024-04-10 17:35:12', '2024-04-10 17:35:15');


CREATE TABLE `t_user_phone` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `phone` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT '' COMMENT '手机号',
    `user_id` bigint DEFAULT '-1' COMMENT '用户id',
    `status` tinyint DEFAULT '-1' COMMENT '状态(0无效，1有效)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_phone` (`phone`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='用户手机表';
