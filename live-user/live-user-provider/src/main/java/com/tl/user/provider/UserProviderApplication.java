package com.tl.user.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@MapperScan(basePackages={"com.tl.user.provider.mapper"})
public class UserProviderApplication {
    public static void main(String[] args) {
        //简单启动方式
//        SpringApplication.run(UserProviderApplication.class,args);
        //工程中常用的启动方式
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.run(args);
    }
}
