package com.tl.live.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import lombok.Data;
/**
 * Author： roy
 * Description：
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "tllive.gateway")
public class GatewayAppProperties {

    private List<String> whiteUrlList;

}
