package com.smartmobility.usermobilitypassservice.dto;

import com.smartmobility.usermobilitypassservice.entity.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionRequest {

    private SubscriptionType subscriptionType;
}