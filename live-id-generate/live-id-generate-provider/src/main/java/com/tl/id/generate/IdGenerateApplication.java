package com.tl.id.generate;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import javax.swing.*;

/**
 * Author： roy
 * Description：
 **/
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class IdGenerateApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdGenerateApplication.class,args);
    }
}
