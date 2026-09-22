package com.tl.user.provider.util;

import org.springframework.context.annotation.Configuration;

/**
 * Author： roy
 * Description：
 **/
@Configuration
public class UserRedisKeyBuilder {
    private static final String USER_INFO_KEY="userInfo";
    private static final String SPLIT_ITEM = ":";

    public String buildUserInfoKey(Long userId){
        return USER_INFO_KEY + SPLIT_ITEM + userId;
    }

}
