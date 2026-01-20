package com.newsApi.newsApi.repository;

import com.newsApi.newsApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,String> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
}
