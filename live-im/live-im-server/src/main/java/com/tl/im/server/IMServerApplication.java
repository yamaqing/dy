package com.tl.im.server;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.annotation.Resource;

/**
 * Author： roy
 * Description：
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class IMServerApplication{

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IMServerApplication.class);
//        springApplication.setWebApplicationType(WebApplicationType.SERVLET);
        springApplication.run(args);
    }
}
