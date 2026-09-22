package com.tl.im.busi.consumer;

import com.tl.im.busi.service.ChatBusiService;
import com.tl.im.config.IMConstants;
import com.tl.im.protocol.GenericMessage;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Author： roy
 * Description：
 **/
@Component
@RocketMQMessageListener(topic = IMConstants.MQ_TOPIC_CHAT,consumerGroup = "tl-live-im-busi")
public class ChatMessageConsumer implements RocketMQListener<GenericMessage> {
    Logger logger = LoggerFactory.getLogger(ChatMessageConsumer.class);

    @Resource
    private ChatBusiService chatBusiService;
    @Override
    public void onMessage(GenericMessage genericMessage) {
        logger.info("接收到消息：{}",genericMessage);
        chatBusiService.handleMessage(genericMessage);
    }
}
