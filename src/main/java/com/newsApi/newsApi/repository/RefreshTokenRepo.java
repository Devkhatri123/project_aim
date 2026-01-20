package com.newsApi.newsApi.repository;

import com.newsApi.newsApi.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, String> {
    RefreshToken findOneByRefreshToken(String refreshToken);
    @Modifying
    @Query(value = "delete from RefreshToken refreshToken where refreshToken.id=:id")
    void deleteToken(@Param("id") String id);
}
