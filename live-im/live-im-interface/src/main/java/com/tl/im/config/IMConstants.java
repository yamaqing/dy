package com.tl.im.config;

/**
 * Author： roy
 * Description：
 **/
public class IMConstants {

    public static final String PROP_USER_ID="UserId";

    public static final String MESSAGE_HEARTBEAT="Heartbeat";

    //消息类型：加入房间
    public static final int MESSAGE_TYPE_JOIN_ROOM=0;
    public static final int MESSAGE_TYPE_EXIT_ROOM=1;
    public static final int MESSAGE_TYPE_CHAT=2;

    public static final int MESSAGE_TYPE_GIFT=5;

    public static final String MQ_TOPIC_CHAT="CHAT_TOPIC";
}
