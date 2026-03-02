package com.smartmobility.tripmanagementservice.client;

import com.smartmobility.tripmanagementservice.client.fallback.MobilityPassClientFallback;
import com.smartmobility.tripmanagementservice.dto.MobilityPassResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-mobility-pass-service", fallback = MobilityPassClientFallback.class)
public interface MobilityPassClient {

    @GetMapping("/api/mobility-passes/number/{passNumber}")
    MobilityPassResponseDto getMobilityPassByPassNumber(@PathVariable String passNumber);

    @GetMapping("/api/mobility-passes/user/{userId}")
    MobilityPassResponseDto getMobilityPassByUserId(@PathVariable java.util.UUID userId);
}