package com.newsApi.newsApi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private Object data;
    private int statusCode;
}
