package com.tl.user.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tl.common.redis.UserCacheKeyBuilder;
import com.tl.live.enums.CommonStatusEnum;
import com.tl.live.util.ConvertBeanUtils;
import com.tl.live.util.DESUtil;
import com.tl.user.dto.UserLoginDTO;
import com.tl.user.dto.UserPhoneDTO;
import com.tl.user.provider.entity.UserPhoneDO;
import com.tl.user.provider.mapper.UserPhoneMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Author： roy
 * Description：
 **/
@Service
public class UserMobileService {

    @Resource
    private UserCacheKeyBuilder userCacheKeyBuilder;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private UserPhoneMapper userPhoneMapper;

    @Resource
    private UserService userService;

    public UserLoginDTO login(String mobile) {
        //参数校验
        if (!StringUtils.hasText(mobile)) {
            return null;
        }
        //检查手机号是否注册过
        UserPhoneDTO userPhoneDTO = this.queryByPhone(mobile);
        //如果注册过，创建token，返回userId
        if (userPhoneDTO != null) {
            return UserLoginDTO.loginSuccess(userPhoneDTO.getUserId());
        }
        //如果没有注册过，生成user信息，插入手机记录，绑定userId。
        return registerAndLogin(mobile);
    }

    private UserLoginDTO registerAndLogin(String mobile) {
        //生成用户信息 并绑定手机号码
        UserLoginDTO userLoginDTO = userService.generateDefaultUserByMobile(mobile);
        //清除手机号查询缓存
        redisTemplate.delete(userCacheKeyBuilder.buildUserPhoneKey(mobile));
        return userLoginDTO;
    }

    private UserPhoneDTO queryByPhone(String mobile) {

        String redisKey = userCacheKeyBuilder.buildUserPhoneKey(mobile);
        UserPhoneDTO userPhoneDTO = (UserPhoneDTO) redisTemplate.opsForValue().get(redisKey);
        if (userPhoneDTO != null) {
            //属于空值缓存对象
            if (userPhoneDTO.getUserId() == null) {
                return null;
            }
            return userPhoneDTO;
        }
        userPhoneDTO = this.queryByPhoneFromDB(mobile);
        if (userPhoneDTO != null) {
            userPhoneDTO.setPhone(DESUtil.decrypt(userPhoneDTO.getPhone()));
            redisTemplate.opsForValue().set(redisKey, userPhoneDTO, 30, TimeUnit.MINUTES);
            return userPhoneDTO;
        }
        //缓存击穿，空值缓存
        userPhoneDTO = new UserPhoneDTO();
        redisTemplate.opsForValue().set(redisKey, userPhoneDTO, 5, TimeUnit.SECONDS);
        return null;
    }

    private UserPhoneDTO queryByPhoneFromDB(String mobile) {
        LambdaQueryWrapper<UserPhoneDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPhoneDO::getPhone, DESUtil.encrypt(mobile));
        queryWrapper.eq(UserPhoneDO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        UserPhoneDO userPhoneDO = userPhoneMapper.selectOne(queryWrapper);
        UserPhoneDTO userPhoneDTO = ConvertBeanUtils.convert(userPhoneDO, UserPhoneDTO.class);
        return userPhoneDTO;

    }
}
