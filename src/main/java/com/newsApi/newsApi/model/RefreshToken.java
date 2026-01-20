package com.newsApi.newsApi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    @Column(name = "user_id")
    private String user_id;
    private String refreshToken;
    private Instant expiry;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}
