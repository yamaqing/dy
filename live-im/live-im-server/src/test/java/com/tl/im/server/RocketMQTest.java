package com.tl.im.server;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Author： roy
 * Description：
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class RocketMQTest {

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Test
    public void sendMessage(){
        rocketMQTemplate.convertAndSend("test-topic","hello world");
    }
}
