package com.newsApi.newsApi.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpDTO {
    private int otp;
    private String email;
    private String verificationType;

}