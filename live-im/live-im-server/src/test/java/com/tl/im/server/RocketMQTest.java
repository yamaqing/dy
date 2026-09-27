package com.tl.im.server;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * Author： roy
 * Description：
 **/
@SpringBootTest
public class RocketMQTest {

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Test
    public void sendMessage(){
        rocketMQTemplate.convertAndSend("test-topic","hello world");
    }
}
