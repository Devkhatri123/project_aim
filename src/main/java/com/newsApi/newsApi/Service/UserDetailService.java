package com.newsApi.newsApi.Service;

import com.newsApi.newsApi.model.User;
import com.newsApi.newsApi.model.UserDetails;
import com.newsApi.newsApi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepo userRepo;
    @Autowired
    public UserDetailService(final UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepo.findByEmail(username);
       if(user == null){
           throw new UsernameNotFoundException("user not found");
       }
       return new com.newsApi.newsApi.model.UserDetails(user);
    }
}
