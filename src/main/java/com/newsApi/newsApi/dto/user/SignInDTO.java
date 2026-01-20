package com.newsApi.newsApi.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDTO {
    private String email;
    private String password;
}
