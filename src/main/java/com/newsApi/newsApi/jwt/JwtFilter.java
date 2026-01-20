package com.newsApi.newsApi.jwt;

import com.newsApi.newsApi.Service.JwtService;
import com.newsApi.newsApi.Service.UserDetailService;
import com.newsApi.newsApi.model.UserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailService userDetailService;
    //@Qualifier("handlerExceptionResolver")
    //private final HandlerExceptionResolver resolver;

    @Autowired
    public JwtFilter(final JwtService jwtService, final UserDetailService userDetailService){
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String authorizationHeader = request.getHeader("Authorization");
    String email = null;
    String token = null;
    if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
        token = authorizationHeader.substring(7);
    }
    if(token != null){
        try{
         email = jwtService.extractEmail(token);
        }catch (IllegalArgumentException e){
           // resolver.resolveException(request, response, null, e);
            log.error("IllegalArgumentException:", e);
            return;
        }catch (MalformedJwtException e){
           // resolver.resolveException(request, response, null, e);
            log.error("Token has been malformed: {}", e.getMessage());
            return;
        }catch (ExpiredJwtException e){
           // resolver.resolveException(request, response, null, e);
            log.error("Token has been expired: {}", e.getMessage());
            return;
        }catch (Exception e){
            // resolver.resolveException(request, response, null, e);
            log.error("Exception:", e);
            return;
        }
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
             UserDetails user = null;
            try{
                user = userDetailService.loadUserByUsername(email);
            }catch (UsernameNotFoundException e){
                log.error("username not found");
            }
            boolean isTokenValid = jwtService.isTokenValid(token,user);
            if(isTokenValid){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }
    filterChain.doFilter(request, response);
    }
}
