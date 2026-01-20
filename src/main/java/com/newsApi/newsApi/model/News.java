package com.newsApi.newsApi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(indexes = {@Index(name = "category_idx",columnList = "category")})
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String headline;
    @Column(length = 600)
    private String Summary;
    private String category;
    private boolean isDebunk;
    private int ConfidenceScore;
    @Column(length = 350)
    private String sourceName;
    private String thumbnail;
    private LocalDateTime pubDate;

    @ManyToMany(mappedBy = "savedNews")
    @JsonIgnore
    private List<User> users;
}
