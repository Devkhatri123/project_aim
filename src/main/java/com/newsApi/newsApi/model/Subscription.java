package com.newsApi.newsApi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Subscription {
    @Id
    @Column(name = "user_id")
    private String user_id;
    private float amount;
    private String currency;
    private String state;
    private LocalDate startDateTime;
    private LocalDate expiresOn;
    private boolean isExpired;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}
