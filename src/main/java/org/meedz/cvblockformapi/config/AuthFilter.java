package org.meedz.cvblockformapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Configuration
public class AuthFilter extends OncePerRequestFilter {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String clientId;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String xAuth = request.getHeader("Authorization");//here is your token value
        String cors = request.getHeader("Access-Control-Allow-Origin");//here is your token value
        System.out.println(cors);
        try {
            if (xAuth != null) {
                String[] chunks = xAuth.split("\\.");
                Base64.Decoder decoder = Base64.getUrlDecoder();
                String payload = new String(decoder.decode(chunks[1]));
                if (!payload.contains(clientId)) {
                    response.sendError(401);
                }
            } else {
                response.sendError(401);
            }
        } catch (Exception exception) {
            response.sendError(401);
        }
        filterChain.doFilter(request, response);
    }

}
