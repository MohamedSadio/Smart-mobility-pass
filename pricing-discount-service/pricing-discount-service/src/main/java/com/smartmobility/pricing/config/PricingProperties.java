package com.smartmobility.pricing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "pricing.rules")
@Data
public class PricingProperties {

    private BigDecimal baseRatePerKm = BigDecimal.valueOf(100);
    private OffPeak offPeak = new OffPeak();
    private Loyalty loyalty = new Loyalty();
    private BigDecimal dailyCap = BigDecimal.valueOf(3000);

    @Data
    public static class OffPeak {
        private boolean enabled = true;
        private BigDecimal discountRate = BigDecimal.valueOf(0.10);
        private int startHour = 20;
        private int endHour = 6;
    }

    @Data
    public static class Loyalty {
        private boolean enabled = true;
        private BigDecimal discountRate = BigDecimal.valueOf(0.05);
        private int minTrips = 10;
    }
}
