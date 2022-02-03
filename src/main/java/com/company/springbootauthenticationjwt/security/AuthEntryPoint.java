package com.company.springbootauthenticationjwt.security;

import com.company.springbootauthenticationjwt.util.HttpUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {
    private final HttpUtil httpUtil;

    public AuthEntryPoint(HttpUtil httpUtil){
        this.httpUtil = httpUtil;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("uri", request.getRequestURI());
        map.put("msg", authException.getMessage() != null ? authException.getMessage() : "Authentication failed");

        httpUtil.printResponse(response, map, HttpServletResponse.SC_UNAUTHORIZED);
    }

}
