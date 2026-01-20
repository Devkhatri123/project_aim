package com.newsApi.newsApi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
    private String errorMessage;
    private int statusCode;
}
