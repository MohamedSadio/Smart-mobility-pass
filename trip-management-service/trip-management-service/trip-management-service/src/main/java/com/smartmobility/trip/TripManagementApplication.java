package com.smartmobility.trip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TripManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(TripManagementApplication.class, args);
    }
}
