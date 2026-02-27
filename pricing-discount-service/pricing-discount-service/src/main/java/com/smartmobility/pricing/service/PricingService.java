package com.smartmobility.pricing.service;

import com.smartmobility.pricing.dto.PricingDto;

public interface PricingService {
    PricingDto.PricingResponse calculatePrice(PricingDto.PricingRequest request);
}