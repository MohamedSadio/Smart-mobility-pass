package com.smartmobility.pricing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "pricing")
@Data
public class PricingProperties {

    private Map<String, BigDecimal> baseFare = new HashMap<>();
    private Map<String, BigDecimal> pricePerKm = new HashMap<>();
    private Map<String, BigDecimal> dailyCap = new HashMap<>();

    private Discount discount = new Discount();
    private OffPeak offPeak = new OffPeak();
    private Loyalty loyalty = new Loyalty();

    @Data
    public static class Discount {
        private Integer offPeak;
        private LoyaltyDiscount loyalty = new LoyaltyDiscount();
        private SubscriptionDiscount subscription = new SubscriptionDiscount();
    }

    @Data
    public static class LoyaltyDiscount {
        private Integer bronze;
        private Integer silver;
        private Integer gold;
    }

    @Data
    public static class SubscriptionDiscount {
        private Integer monthly;
        private Integer annual;
    }

    @Data
    public static class OffPeak {
        private LocalTime startTime;
        private LocalTime endTime;
    }

    @Data
    public static class Loyalty {
        private Integer bronzeThreshold;
        private Integer silverThreshold;
        private Integer goldThreshold;
    }
}