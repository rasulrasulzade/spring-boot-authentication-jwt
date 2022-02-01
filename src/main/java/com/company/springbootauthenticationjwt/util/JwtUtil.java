package com.company.springbootauthenticationjwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${token.secret}")
    private String secret;

    @Value("${token.expiration_time}")
    private String tokenExpTime;


    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
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

    public String createToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(tokenExpTime)))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
