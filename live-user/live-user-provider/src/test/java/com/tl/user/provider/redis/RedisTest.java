package com.tl.user.provider.redis;

import com.tl.user.dto.UserDTO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Author： roy
 * Description：
 **/
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void ObjTest(){
        UserDTO user = new UserDTO();
        user.setUserId(1L);
        user.setNickName("test");
        user.setAvatar("/img/xxxxx.png");

        redisTemplate.opsForValue().set("123",user);
        System.out.println("对象缓存设置成功");
        Object o = redisTemplate.opsForValue().get("123");
        System.out.println("获取缓存对象："+o);
        System.out.println("转换成UserDTO对象："+(UserDTO)o);
    }

}
