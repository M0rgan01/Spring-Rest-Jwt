package com.test.business;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;

public interface JwtService {
 public Collection<GrantedAuthority> getListAuthorities(Claims claims);
 public String createAuthToken(String userName, Collection<GrantedAuthority> collection);
 public String createRefreshToken(String userName, Collection<GrantedAuthority> collection);
 public Claims validateRefreshToken(String token);
}
