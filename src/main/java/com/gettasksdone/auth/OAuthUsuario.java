package com.gettasksdone.auth;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuthUsuario implements OAuth2User {

    private OAuth2User oauthUser;

    public OAuthUsuario(OAuth2User oauthUser){
        this.oauthUser = oauthUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauthUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauthUser.getAuthorities();
    }

    @Override
    public String getName() {
        return oauthUser.getName();
    }

    public String getEmail(){
        return oauthUser.<String>getAttribute("email");
    }
    
}
