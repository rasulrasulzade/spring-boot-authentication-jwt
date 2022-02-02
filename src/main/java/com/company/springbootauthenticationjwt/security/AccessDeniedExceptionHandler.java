package com.company.springbootauthenticationjwt.security;

import com.company.springbootauthenticationjwt.util.ExceptionUtil;
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
    private final ExceptionUtil exceptionUtil;

    public AccessDeniedExceptionHandler(ExceptionUtil exceptionUtil){
        this.exceptionUtil = exceptionUtil;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("uri", request.getRequestURI());
        map.put("msg", accessDeniedException.getMessage() != null ? accessDeniedException.getMessage() : "ACCESS DENIED");

        exceptionUtil.printException(response, map, HttpServletResponse.SC_FORBIDDEN);

    }
}
