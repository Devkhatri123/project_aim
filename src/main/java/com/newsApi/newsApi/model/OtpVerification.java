package com.newsApi.newsApi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OtpVerification {
    @Id
    private String user_id;
    private int otp;
    private LocalDateTime expiresAt;
    private String verificationType;
    @OneToOne
    @MapsId
    @JsonIgnore
    private User user;
}
