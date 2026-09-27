package com.tl.im.protocol;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
/**
 * Author： roy
 * Description：协议主体
 **/
@Data
public class GenericMessage implements Serializable {

    /**
     * 消息类型：
     */
    private Integer type;

    /**
     * 房间ID
     */
    private Long roomId;

    private Long fromUserId;

    private String fromUserName;

    /**
     * 消息体
     */
    private List<MessageBody> body;
}
