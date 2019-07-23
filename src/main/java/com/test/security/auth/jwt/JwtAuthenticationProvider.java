package com.test.security.auth.jwt;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.test.entities.Roles;
import com.test.security.SecurityConstants;
import com.test.security.auth.model.UserContext;
import com.test.security.token.JwtAuthenticationToken;
import com.test.security.token.JwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * Processus d'authentification par JWT 
 * 
 * 
 * @author Pichat morgan
 *
 * 20 Juillet 2019
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {  
  

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	
        JwtToken jwtToken = (JwtToken) authentication.getCredentials();

        //on récupère les claimes, tout en vérifiant le token
        Jws<Claims> jwsClaims = jwtToken.parseClaims(jwtToken.getToken());
        
        // récupère le sujet (username)
        String subject = jwsClaims.getBody().getSubject();
        
        @SuppressWarnings("unchecked")
		List<String> listRoles = jwsClaims.getBody().get(SecurityConstants.AUTHORITIES_PREFIX, List.class);
        
        //création d'un utilisateur grace au nom à la liste de role contenu dans le token
        UserContext context = UserContext.create(subject, Roles.getListAuthorities(listRoles));
          
        return new JwtAuthenticationToken(context, context.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
