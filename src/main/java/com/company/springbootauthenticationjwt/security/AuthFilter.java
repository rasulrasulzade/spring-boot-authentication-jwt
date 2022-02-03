package com.company.springbootauthenticationjwt.security;

import com.company.springbootauthenticationjwt.respository.UserRepository;
import com.company.springbootauthenticationjwt.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthFilter(UserRepository repository, JwtUtil jUtil) {
        this.userRepository = repository;
        this.jwtUtil = jUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Get authorization header and validate
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        // Get JWT token and validate it
        String accessToken = authHeader.split(" ")[1].trim();

        if(jwtUtil.isTokenExpired(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get user identity
        UserDetails userDetails = userRepository.findByEmail(jwtUtil.getEmail(accessToken));

        if (!jwtUtil.validate(accessToken, userDetails)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Set user on the spring security context
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails == null ? Arrays.asList() : userDetails.getAuthorities());
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
