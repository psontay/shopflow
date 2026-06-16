package com.shopflow.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShopflowInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopflowInventoryApplication.class, args);
    }

}
