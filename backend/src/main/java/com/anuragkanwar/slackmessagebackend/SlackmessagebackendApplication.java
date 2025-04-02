package com.anuragkanwar.slackmessagebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SlackmessagebackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlackmessagebackendApplication.class, args);
    }

}
