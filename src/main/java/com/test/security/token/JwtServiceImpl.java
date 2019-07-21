package com.test.security.token;



import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.test.business.ContactService;
import com.test.entities.Contact;
import com.test.security.SecurityConstants;
import com.test.security.auth.model.UserContext;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 
 * @author pichat morgan
 *
 * 20 Juillet 2019
 *
 */
@Service
public class JwtServiceImpl implements JwtService{

	
	@Autowired
	private ContactService contactService;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GrantedAuthority> getListAuthorities(Claims claims) {
							
		// on recupere la liste des roles
		List<String> roles = claims.get(SecurityConstants.AUTHORITIES_PREFIX, List.class);

		// on l'assigne a une list spring d'authorities
		List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
							
		return authorities;
	}

	@Override
	public String createAuthToken(UserContext userContext) {
			if (userContext.getUsername() == null || userContext.getUsername().isEmpty()) 
	            throw new IllegalArgumentException("Cannot create JWT Token without username");

	        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) 
	            throw new IllegalArgumentException("User doesn't have any privileges");
	        
	        	//construction du Json web token
				String jwt = Jwts.builder()
				.setSubject(userContext.getUsername()) // ajout de username
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME_AUTH_TOKEN)) // ajout d'une date d'expiration
				.signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET) // partie secrete servant de clé, avec un algorithme de type HS 256
				.claim(SecurityConstants.AUTHORITIES_PREFIX, userContext.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList())) // ajout personnalisé --> on ajoute les roles
				.compact(); // construction du token
		return jwt;
	}

	@Override
	public String createRefreshToken(UserContext userContext) {	
		
		if (userContext.getUsername() == null || userContext.getUsername().isEmpty()) 
            throw new IllegalArgumentException("Cannot create JWT Token without username");
		
		//construction du Json web token
		String jwt = Jwts.builder()
				.setSubject(userContext.getUsername()) // ajout de username
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME_REFRESH_TOKEN)) // ajout d'une date d'expiration
				.signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET) // partie secrete servant de clé, avec un algorithme de type HS 256
				.claim(SecurityConstants.REFRESH_ACTIVE_PREFIX, true) // ajout de vérification pour le rafraichissement
				.compact(); // construction du token
		return jwt;
	}

		
	@Override
	public UserContext validateRefreshToken(String token) {
		// on récupère le token dans un object Claims
		Claims claims = Jwts.parser().setSigningKey(SecurityConstants.SECRET) // on assigne le secret
				.parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, "")) // on enlève le préfixe
				.getBody(); // on récupère le corp
		
		// on recupere le contact pour comparaison
		Contact contact = contactService.findContactByUserName(claims.getSubject()).orElseThrow(() -> new UsernameNotFoundException("User not found: " + claims.getSubject()));
					
		//si le contact est toujours bon, alors le token est toujours valide
		if(contact.isActive() == (boolean) claims.get(SecurityConstants.REFRESH_ACTIVE_PREFIX))
			return UserContext.create(claims.getSubject(), getListAuthorities(claims));
		
		return null;
	}

	
	@Override
	public String extract(String header) {
	      if (header == null || header.isEmpty()) {
	            throw new AuthenticationServiceException("Authorization header cannot be blank!");
	        }

	        return header.substring(SecurityConstants.TOKEN_PREFIX.length(), header.length());	
	}
	
}
