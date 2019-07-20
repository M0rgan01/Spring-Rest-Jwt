package com.test.security.token;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.test.security.auth.model.UserContext;

import io.jsonwebtoken.Claims;

/**
 * 
 * @author pichat morgan
 *
 * 20 Juillet 2019
 *
 */
public interface JwtService {
 public List<GrantedAuthority> getListAuthorities(Claims claims);
 public String createAuthToken(UserContext userContext);
 public String createRefreshToken(UserContext userContext);
 public Claims validateRefreshToken(String token);
 public String extract(String header);
}
