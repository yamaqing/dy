package com.tl.user.provider.impl;

import com.tl.user.dto.MsgCheckDTO;
import com.tl.user.dto.UserDTO;
import com.tl.user.dto.UserLoginDTO;
import com.tl.user.inter.IUserRPCService;
import com.tl.user.provider.entity.UserDO;
import com.tl.user.provider.mapper.UserMapper;
import com.tl.user.provider.service.SmsService;
import com.tl.user.provider.service.UserService;
import com.tl.user.provider.util.UserRedisKeyBuilder;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Dubbo服务实现类
 */
@DubboService
public class UserRPCService implements IUserRPCService {

    private Logger logger = LoggerFactory.getLogger(UserRPCService.class);

    @Resource
    private SmsService smsService;

    @Resource
    private UserService userService;

    @Override
    public UserDTO getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    @Override
    public boolean sendLoginCode(String mobile) {
        return smsService.sendLoginCode(mobile);
    }

    @Override
    public MsgCheckDTO checkLoginCode(String mobile, Integer code) {
        return smsService.checkSmsCode(mobile,code);
    }

    @Override
    public String createAndSaveLoginToken(Long userId) {
        return userService.createAndSaveLoginToken(userId);
    }

    @Override
    public String checkToken(String token) {
        return userService.checkToken(token);
    }
}
