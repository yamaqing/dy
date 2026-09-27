package com.tl.im.protocol;

import java.io.Serializable;

import lombok.Data;
/**
 * Author： roy
 * Description：协议内容
 * 注：该对象会存入 Redis Set 做房间消息聚合（见 ChatBusiService）。
 * Redis SADD/SREM 按序列化字节判重，msgId 唯一性由分布式 ID 生成器保证，
 * equals/hashCode 语义不影响该链路，可安全使用 @Data。
 **/
@Data
public class MessageBody implements Serializable {

    private Long msgId;

    private String content;

    private Long userId;

    private String userName;
}
