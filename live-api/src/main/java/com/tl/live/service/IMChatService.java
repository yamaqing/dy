package com.tl.live.service;

import com.tl.im.inter.IIMRPCService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * Author： roy
 * Description：
 **/
@Service
public class IMChatService {

    @DubboReference(check = false)
    private IIMRPCService iimrpcService;

    public boolean sendRoomBroadCast(Long roomId, String message)
    {
        return iimrpcService.sendMesasgeToRoom(roomId,message);
    }
}
