package com.tl.im.server.impl;

import com.tl.im.config.IMConstants;
import com.tl.im.inter.IIMRPCService;
import com.tl.im.protocol.GenericMessage;
import com.tl.im.protocol.MessageBody;
import com.tl.im.server.service.MessageHandlerService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Author： roy
 * Description：
 **/

@DubboService
public class IMRPCService implements IIMRPCService {
    private Logger logger = LoggerFactory.getLogger(IMRPCService.class);

    @Resource
    private MessageHandlerService messageHandlerService;
    @Override
    public boolean sendMesasgeToRoom(Long roomId, String message) {
        GenericMessage genericMessage = new GenericMessage();
        genericMessage.setRoomId(roomId);
        genericMessage.setType(IMConstants.MESSAGE_TYPE_CHAT);

        List<MessageBody> messageBodies = new ArrayList<>();

        MessageBody messageBody = new MessageBody();
        messageBody.setContent(message);
        messageBodies.add(messageBody);

        genericMessage.setBody(messageBodies);
        return messageHandlerService.sendRoomBroadCast(roomId,genericMessage);
    }

    @Override
    public boolean pushChatMessage(Long roomId, GenericMessage genericMessage) {
        return messageHandlerService.pushChatMessage(roomId,genericMessage);
    }
}
