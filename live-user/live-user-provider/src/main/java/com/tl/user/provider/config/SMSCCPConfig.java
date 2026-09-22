package com.tl.user.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Author： roy
 * Description：容联云短信平台配置项
 **/

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

    public String getSmsServerIp() {
        return smsServerIp;
    }

    public void setSmsServerIp(String smsServerIp) {
        this.smsServerIp = smsServerIp;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAccountSId() {
        return accountSId;
    }

    public void setAccountSId(String accountSId) {
        this.accountSId = accountSId;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTestPhone() {
        return testPhone;
    }

    public void setTestPhone(String testPhone) {
        this.testPhone = testPhone;
    }


    public Boolean getTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "SMSCCPConfig{" +
                "smsServerIp='" + smsServerIp + '\'' +
                ", port=" + port +
                ", accountSId='" + accountSId + '\'' +
                ", accountToken='" + accountToken + '\'' +
                ", appId='" + appId + '\'' +
                ", testPhone='" + testPhone + '\'' +
                ", test=" + test +
                '}';
    }
}
