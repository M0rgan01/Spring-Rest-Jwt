package com.test.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.business.JwtService;
import com.test.entities.Contact;

/**
 * Filtre qui intervient AVANT l'authentification par les classes UserDetailsService
 * 
 * @author pichat morgan 
 *
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private ObjectMapper objectMapper;
	private AuthenticationManager authenticationManager;
	private JwtService jwtService;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.objectMapper = objectMapper;	
		this.jwtService = jwtService;
	}

	//Départ de la demande d'authentification
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		Contact contact = null;

		// on récupère la classe Contact depuis la requete
		try {
			contact = objectMapper.readValue(request.getInputStream(), Contact.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		// on fait une demande d'authentification, qui elle va faire appelle à la méthode loadUserByUsername de la classe AccountsericeImpl
		return authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(contact.getUserName(), contact.getPassWord()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
									
		//recuperation de l'objet spring utilisateur 
		User springUser = (User) authResult.getPrincipal();
		
		//construction du Json web token		
		String jwt = jwtService.createToken(springUser.getUsername(), springUser.getAuthorities());
		
		
		//on ajoute le token à l'en-tête de la réponse
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + jwt);
		
	}

}
