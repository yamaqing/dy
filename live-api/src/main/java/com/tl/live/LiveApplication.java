package com.tl.live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author：  roy
 * Description：
 **/
@SpringBootApplication
public class LiveApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LiveApplication.class);
        springApplication.run(args);
    }
}
