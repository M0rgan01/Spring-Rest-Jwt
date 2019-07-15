package com.test.business;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;

public interface JwtService {
 public Collection<GrantedAuthority> getListAuthorities(Claims claims);
 public String createToken(String userName, Collection<GrantedAuthority> collection);
}
