package com.newsApi.newsApi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetToken {
    @Id
    public String user_id;
    private String token;
    private LocalDateTime expiresAt;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

}
