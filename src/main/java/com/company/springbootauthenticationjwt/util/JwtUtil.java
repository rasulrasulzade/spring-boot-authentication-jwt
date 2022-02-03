package com.company.springbootauthenticationjwt.util;

import com.company.springbootauthenticationjwt.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${token.secret}")
    private String secret;

    @Value("${token.access-token-expiration-time}")
    private String accessTokenExpTime;

    @Value("${token.refresh-token-expiration-time}")
    private String refreshTokenExpTime;


    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        Date expDate = extractAllClaims(token).getExpiration();
        return expDate.before(new Date());
    }

    public String getEmail(String token) {
        return (String) extractAllClaims(token).get("email");
    }

    public boolean validate(String token,UserDetails userDetails) {
        String email = getEmail(token);
        return  email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String generateToken(Map<String, Object> userData, String tokenExpTime){
        return Jwts.builder()
                .claim("email", userData.get("email"))
                .claim("roles", userData.get("roles").toString())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(tokenExpTime)))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String generateAccessToken(Map<String, Object> userData) {
        return generateToken(userData, accessTokenExpTime);
    }

    public String generateRefreshToken(Map<String, Object> userData) {
        return generateToken(userData, refreshTokenExpTime);
    }
}
