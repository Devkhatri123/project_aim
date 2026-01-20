package com.newsApi.newsApi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionRequest {
    private int amount;
    private String subscriptionType;
}
