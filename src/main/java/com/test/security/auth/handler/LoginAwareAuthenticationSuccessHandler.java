package com.test.security.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.test.security.SecurityConstants;
import com.test.security.auth.model.UserContext;
import com.test.security.token.JwtService;

/**
 * 
 * Processus de succès de connection personnalisé
 * 
 * @author Pichat morgan
 *
 * 20 Juillet 2019
 * 
 */
@Component
public class LoginAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    @Autowired
    public LoginAwareAuthenticationSuccessHandler(final JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
    	
    	System.out.println("Entrée onAuthenticationSuccess de LoginAwareAuthenticationSuccessHandler");
    	
        UserContext userContext = (UserContext) authentication.getPrincipal();
        
        //création des token
        String accessToken = jwtService.createAuthToken(userContext);
        String refreshToken = jwtService.createRefreshToken(userContext);
        
        //on met en place le code de la réponse
        response.setStatus(HttpStatus.OK.value());
           
       //on ajoute le token d'auth et le token de refresh à l'en-tête de la réponse
      	response.addHeader(SecurityConstants.HEADER_AUTH_STRING, SecurityConstants.TOKEN_PREFIX + accessToken);
      	response.addHeader(SecurityConstants.HEADER_REFRESH_STRING, SecurityConstants.TOKEN_PREFIX + refreshToken);
        clearAuthenticationAttributes(request);
        
        System.out.println("Sortie onAuthenticationSuccess de LoginAwareAuthenticationSuccessHandler");
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process..
     * 
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
