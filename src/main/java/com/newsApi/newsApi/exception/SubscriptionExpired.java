package com.newsApi.newsApi.exception;

public class SubscriptionExpired extends RuntimeException {
    public SubscriptionExpired(String message) {
        super(message);
    }
}
