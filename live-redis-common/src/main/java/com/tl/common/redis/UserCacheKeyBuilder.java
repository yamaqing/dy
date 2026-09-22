package com.tl.common.redis;

import org.springframework.context.annotation.Configuration;

/**
 * Author： roy
 * Description：
 **/
@Configuration
public class UserCacheKeyBuilder extends RedisKeyBuilder {

    private static String USER_PHONE_KEY = "userPhone";
    private static String ACCOUNT_TOKEN_KEY = "account";

    public String buildUserPhoneKey(String phone) {
        return super.getPrefix() + USER_PHONE_KEY + super.getSplitItem() + phone;
    }
    public String buildUserLoginTokenKey(String key) {
        return super.getPrefix() + ACCOUNT_TOKEN_KEY + super.getSplitItem() + key;
    }
}
