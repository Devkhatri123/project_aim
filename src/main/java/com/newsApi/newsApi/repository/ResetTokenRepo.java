package com.newsApi.newsApi.repository;

import com.newsApi.newsApi.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetTokenRepo extends JpaRepository<ResetToken, String> {
    ResetToken findOneByToken(String token);
}
