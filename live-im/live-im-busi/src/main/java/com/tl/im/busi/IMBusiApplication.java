package com.tl.im.busi;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Author： roy
 * Description：
 **/
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class IMBusiApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IMBusiApplication.class);
        springApplication.run(args);
    }
}
