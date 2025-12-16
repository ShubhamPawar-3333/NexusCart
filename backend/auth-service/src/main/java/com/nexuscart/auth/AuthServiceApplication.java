package com.nexuscart.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Auth Service Application
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = { "com.nexuscart.auth", "com.nexuscart.security" })
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
