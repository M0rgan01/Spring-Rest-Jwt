package com.test.security.auth.login;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.entities.Contact;
import com.test.security.exception.AuthMethodNotSupportedException;

/**
 * 
 * Filtre de mise en place d'authentification par Login 
 * 
 * @author Pichat morgan
 *
 * 20 Juillet 2019
 * 
 */
public class LoginProcessingFilter extends AbstractAuthenticationProcessingFilter {
	
    private static Logger logger = LoggerFactory.getLogger(LoginProcessingFilter.class);
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final ObjectMapper objectMapper;
    
       
    public LoginProcessingFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler, 
            AuthenticationFailureHandler failureHandler, ObjectMapper mapper) {
        super(defaultProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
    	
    	 System.out.println("Entrée attemptAuthentication de LoginProcessingFilter");
    	
    	// vérification que la méthode soit bien POST
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            if(logger.isDebugEnabled()) {
                logger.debug("Authentication method not supported. Request method: " + request.getMethod());
            }
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }

        //récupération du json contenu dans le corp de requete
        Contact contact = objectMapper.readValue(request.getInputStream(), Contact.class);
        
        //vérification de la validité de l'object
        if(contact.getUserName() == null || contact.getUserName().isEmpty() || contact.getPassWord() == null || contact.getPassWord().isEmpty())
        	throw new AuthenticationServiceException("Un champ n'est pas rempli");	
        	
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(contact.getUserName(), contact.getPassWord());

        System.out.println("Sortie attemptAuthentication de LoginProcessingFilter");
        
        //processus d'authentification
        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
