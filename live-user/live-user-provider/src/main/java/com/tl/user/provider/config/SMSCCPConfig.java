package com.tl.user.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
/**
 * Author： roy
 * Description：容联云短信平台配置项
 **/

@Data
@Configuration
@ConfigurationProperties(prefix = "tllive.sms.ccp")
public class SMSCCPConfig {
    private String smsServerIp;
    private Integer port;
    private String accountSId;
    private String accountToken;
    private String appId;
    private String testPhone;

    private Boolean test = false;

}
