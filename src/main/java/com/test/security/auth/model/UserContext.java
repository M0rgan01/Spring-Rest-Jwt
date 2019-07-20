package com.test.security.auth.model;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * @author Pichat morgan
 *
 * Juill 20, 2019
 */
public class UserContext {
    private final String username;
    private final List<GrantedAuthority> authorities;

    private UserContext(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }
    
    public static UserContext create(String username, List<GrantedAuthority> authorities) {      
        return new UserContext(username, authorities);
    }

    public String getUsername() {
        return username;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
