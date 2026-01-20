package com.newsApi.newsApi.exception.refreshtoken;

public class RefreshTokenExpired extends RuntimeException {
    public RefreshTokenExpired(String message) {
        super(message);
    }
}
