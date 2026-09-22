package com.tl.im.protocol;

import java.io.Serializable;

/**
 * Author： roy
 * Description：协议内容
 **/
public class MessageBody implements Serializable {

    private Long msgId;

    private String content;

    private Long userId;

    private String userName;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "MessageBody{" +
                "msgId=" + msgId +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
