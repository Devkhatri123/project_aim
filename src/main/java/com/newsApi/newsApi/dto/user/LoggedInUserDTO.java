package com.newsApi.newsApi.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggedInUserDTO {
    private String name;
    private String email;
    private boolean hasSubscription;
    private boolean isEmailVerified;
}
