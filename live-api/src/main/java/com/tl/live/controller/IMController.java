package com.tl.live.controller;

import com.tl.live.entity.WebResDTO;
import com.tl.live.service.IMChatService;
import com.tl.live.service.IMTokenService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Author： roy
 * Description：
 **/
@RestController
@RequestMapping("/im")
public class IMController {

    @Value("${tllive.im.instance}")
    private String imInstance;
    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private IMTokenService imTokenService;

    @Resource
    private IMChatService imChatService;

    /**
     * 获取IM服务器地址
     * @return
     */
    @PostMapping("/getIMServer")
    public WebResDTO getIMServer(String userId){
        List<ServiceInstance> instances = discoveryClient.getInstances(imInstance);
        if(instances.size()  == 0){
            return new WebResDTO(WebResDTO.ERROR_CODE,"IM服务未启动");
        }else{
            //TODO IM-SERVER增加Dubbo服务后，Dubbo服务也会往Nacos上注册一个dubbo服务的实例。这些实例要过滤掉。
            List<ServiceInstance> livinginstances = new ArrayList<>();
            instances.forEach(instance -> {
                if(instance.getPort()<9000){
                    livinginstances.add(instance);
                }
            });
            int index = ThreadLocalRandom.current().nextInt(0, livinginstances.size());
//            items.get(ThreadLocalRandom.current().nextInt(items.size()))
            ServiceInstance instanceToChoose = livinginstances.get(ThreadLocalRandom.current().nextInt(instances.size()));
            //ws://localhost:8989/chat/1
            var instanceUrl = "ws://"+instanceToChoose.getHost()+":"+instanceToChoose.getPort()+"/chat/"+userId;
            var imToken = imTokenService.generateIMToken(userId);
            Map<String ,Object > res = new HashMap<>();
            res.put("imToken",imToken);
            res.put("url",instanceUrl);
            return new WebResDTO(WebResDTO.SUCCESS_CODE,res);
        }
    }

    //TODO 往直播间发送公告。--只用作单机测试。
    // IMServer后端只做了单机测试，没做集群化处理。
    @PostMapping("/sendRoomBroadCast")
    public WebResDTO sendRoomBroadCast(Long roomId,String message){
        if(imChatService.sendRoomBroadCast(roomId,message)){
            return new WebResDTO(WebResDTO.SUCCESS_CODE,"消息发送成功");
        }
        return new WebResDTO(WebResDTO.ERROR_CODE,"未发出消息，请检查直播间是否有客户连接");
    }
}
