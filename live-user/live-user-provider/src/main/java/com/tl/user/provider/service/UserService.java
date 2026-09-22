package com.tl.user.provider.service;

import com.tl.common.redis.UserCacheKeyBuilder;
import com.tl.id.inter.IGenerateIDRPCService;
import com.tl.live.enums.CommonStatusEnum;
import com.tl.live.util.ConvertBeanUtils;
import com.tl.live.util.DESUtil;
import com.tl.user.dto.UserDTO;
import com.tl.user.dto.UserLoginDTO;
import com.tl.user.provider.entity.UserDO;
import com.tl.user.provider.entity.UserPhoneDO;
import com.tl.user.provider.mapper.UserMapper;
import com.tl.user.provider.mapper.UserPhoneMapper;
import com.tl.user.provider.util.UserRedisKeyBuilder;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Author： roy
 * Description：
 **/
@Service
public class UserService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserPhoneMapper userPhoneMapper;

    @Resource
    private UserRedisKeyBuilder keyBuilder;

    @Resource
    private UserCacheKeyBuilder userCacheKeyBuilder;

    @DubboReference(check = false)
    private IGenerateIDRPCService generateIDRPCService;

    public UserDTO getUserById(Long userId) {
        if (null == userId || userId < 0) {
            return null;
        }
        UserDTO userDTO;

        //先从redis查，redis有数据，直接返回
        String userInfoKey = keyBuilder.buildUserInfoKey(userId);
        userDTO = (UserDTO) redisTemplate.opsForValue().get(userInfoKey);
        if (null != userDTO && userDTO.getUserId() > 0) {
            return userDTO;
        }
        //redis中没有，从数据库查
        UserDO userDO = userMapper.selectById(userId);
        //数据库中有，插入redis，返回
        if (null != userDO) {
            userDTO = ConvertBeanUtils.convert(userDO,UserDTO.class);
            redisTemplate.opsForValue().set(userInfoKey, userDTO, 30, TimeUnit.MINUTES);
            return userDTO;
        } else {
            //数据库中没有，为了防止缓存穿透，在redis里插入空值。一段时间内不允许重复到数据库中查。
            UserDTO notExistUser = new UserDTO();
            notExistUser.setUserId(-1L);
            redisTemplate.opsForValue().set(userInfoKey, notExistUser, 30, TimeUnit.SECONDS);
            return null;
        }
    }

    /**
     * 生成用户信息并绑定手机号码
     * @param mobile
     * @return
     */
    public UserLoginDTO generateDefaultUserByMobile(String mobile) {
        UserDO userDO = new UserDO();
        //统一分配主键
        Long userId = generateIDRPCService.getSeqId();
        userDO.setUserId(userId);
        userDO.setNickName("新用户-"+userId);
        userDO.setAvatar("/img/avatar.png");
        userMapper.insert(userDO);

        UserPhoneDO userPhoneDO = new UserPhoneDO();
        //统一分配主键
        userPhoneDO.setId(generateIDRPCService.getSeqId());
        userPhoneDO.setUserId(userId);
        userPhoneDO.setPhone(DESUtil.encrypt(mobile));
        userPhoneDO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        userPhoneMapper.insert(userPhoneDO);

        return UserLoginDTO.loginSuccess(userId);
    }

    public String createAndSaveLoginToken(Long userId) {
        //生成Token
        String token = UUID.randomUUID().toString();
        //将token与用户信息进行绑定
        String userLoginCacheKey = userCacheKeyBuilder.buildUserLoginTokenKey(token);
        //每次登录，三十分钟有效期
        redisTemplate.opsForValue().set(userLoginCacheKey,userId,30,TimeUnit.MINUTES);
        //返回token
        return token;
    }

    public String checkToken(String token) {
        String userLoginCacheKey = userCacheKeyBuilder.buildUserLoginTokenKey(token);

        if (redisTemplate.hasKey(userLoginCacheKey)) {
            return redisTemplate.opsForValue().get(userLoginCacheKey).toString();
        }
        return null;
    }
}
