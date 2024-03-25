package com.gettasksdone.configuration;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SuccessHandler implements AuthenticationSuccessHandler{
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication.getPrincipal() instanceof DefaultOAuth2User){
            DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();
            String username = userDetails.getAttribute("email");
            request.getSession().setAttribute("username", username);
        }
        new DefaultRedirectStrategy().sendRedirect(request, response, "/auth/oauth");
    }
}
