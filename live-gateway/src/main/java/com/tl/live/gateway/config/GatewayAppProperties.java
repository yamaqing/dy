package com.tl.live.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Author： roy
 * Description：
 **/
@Configuration
@ConfigurationProperties(prefix = "tllive.gateway")
public class GatewayAppProperties {

    private List<String> whiteUrlList;

    public List<String> getWhiteUrlList() {
        return whiteUrlList;
    }

    public void setWhiteUrlList(List<String> whiteUrlList) {
        this.whiteUrlList = whiteUrlList;
    }

    @Override
    public String toString() {
        return "GatewayProperties{" +
                "whiteUrlList=" + whiteUrlList +
                '}';
    }
}
