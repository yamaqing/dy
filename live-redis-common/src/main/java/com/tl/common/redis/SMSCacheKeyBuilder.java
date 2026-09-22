package com.tl.common.redis;

import org.springframework.context.annotation.Configuration;

/**
 * Author： roy
 * Description：
 **/
@Configuration
public class SMSCacheKeyBuilder extends RedisKeyBuilder{

    private static String SMS_LOGIN_CODE_KEY = "smsLoginCode";

    public String buildSmsLoginCodeKey(String mobile) {
        return super.getPrefix() + SMS_LOGIN_CODE_KEY + super.getSplitItem() + mobile;
    }
}
