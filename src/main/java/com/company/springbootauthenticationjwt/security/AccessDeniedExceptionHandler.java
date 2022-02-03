package com.company.springbootauthenticationjwt.security;

import com.company.springbootauthenticationjwt.util.HttpUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {
    private final HttpUtil httpUtil;

    public AccessDeniedExceptionHandler(HttpUtil exceptionUtil){
        this.httpUtil = exceptionUtil;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("uri", request.getRequestURI());
        map.put("msg", accessDeniedException.getMessage() != null ? accessDeniedException.getMessage() : "ACCESS DENIED");

        httpUtil.printResponse(response, map, HttpServletResponse.SC_FORBIDDEN);

    }
}
