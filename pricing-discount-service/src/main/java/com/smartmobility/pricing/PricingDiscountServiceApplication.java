package com.smartmobility.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PricingDiscountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricingDiscountServiceApplication.class, args);
	}

}
