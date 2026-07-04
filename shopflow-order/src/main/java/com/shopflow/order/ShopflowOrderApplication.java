package com.shopflow.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.shopflow")
@EnableScheduling
public class ShopflowOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopflowOrderApplication.class, args);
    }

}