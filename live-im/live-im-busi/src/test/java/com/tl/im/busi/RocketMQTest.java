package com.tl.im.busi;

import com.tl.im.config.IMConstants;
import com.tl.im.protocol.GenericMessage;
import com.tl.im.protocol.MessageBody;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

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
    public void sendMQTest(){
        GenericMessage genericMessage = new GenericMessage();
        genericMessage.setType(IMConstants.MESSAGE_TYPE_CHAT);
        genericMessage.setRoomId(2L);
        genericMessage.setFromUserId(212L);

        List<MessageBody> messageBodyList = new ArrayList<>();

        MessageBody messageBody = new MessageBody();
        messageBody.setContent("123123");
        messageBody.setMsgId(123L);

        messageBodyList.add(messageBody);
        genericMessage.setBody(messageBodyList);

        rocketMQTemplate.convertAndSend(IMConstants.MQ_TOPIC_CHAT,genericMessage);
        System.out.println("发送成功");
    }
}
