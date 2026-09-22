package com.tl.live.enums;

public enum GatewayHeaderEnum {
    USER_LOGIN_ID("用户id","tl_live_login_userid");

    String desc;
    String name;

    GatewayHeaderEnum(String desc, String name) {
        this.desc = desc;
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }
}
