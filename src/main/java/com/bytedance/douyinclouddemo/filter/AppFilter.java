package com.bytedance.douyinclouddemo.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AppFilter extends OncePerRequestFilter {
    
    @Value("${server.port}")
    private int httpsPort;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!request.isSecure() && !"localhost".equals(request.getServerName())) {
            String httpsUrl = "https://" + request.getServerName() + ":" + httpsPort + request.getRequestURL();
            if (request.getQueryString() != null) {
                httpsUrl += "?" + request.getQueryString();
            }
            response.sendRedirect(httpsUrl);
        } else {
            filterChain.doFilter(request, response);
        }
                
        
    }
}
