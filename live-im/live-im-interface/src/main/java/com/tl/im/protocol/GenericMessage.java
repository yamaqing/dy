package com.tl.im.protocol;

import java.io.Serializable;
import java.util.List;

/**
 * Author： roy
 * Description：协议主体
 **/
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

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public List<MessageBody> getBody() {
        return body;
    }

    public void setBody(List<MessageBody> body) {
        this.body = body;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    @Override
    public String toString() {
        return "GenericMessage{" +
                "type=" + type +
                ", roomId=" + roomId +
                ", fromUserId=" + fromUserId +
                ", fromUserName='" + fromUserName + '\'' +
                ", body=" + body +
                '}';
    }
}
