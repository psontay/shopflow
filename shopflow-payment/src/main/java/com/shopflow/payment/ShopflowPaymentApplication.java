package com.shopflow.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShopflowPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopflowPaymentApplication.class, args);
    }

}
