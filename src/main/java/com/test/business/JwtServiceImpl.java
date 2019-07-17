package com.test.business;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.test.entities.Contact;
import com.test.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@Service
public class JwtServiceImpl implements JwtService{

	
	@Autowired
	private AccountService accountService;
	
	
	@Override
	public Collection<GrantedAuthority> getListAuthorities(Claims claims) {
				//on recupere la liste des roles
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, String>> roles = (ArrayList<Map<String, String>>) claims.get(SecurityConstants.AUTHORITIES_PREFIX);
				
				//on l'assigne a une collection spring d'authorities
				Collection<GrantedAuthority> authorities = new ArrayList<>();
				roles.forEach(r->{
			authorities.add(new SimpleGrantedAuthority(r.get("authority")));
				});
								
		return authorities;
	}

	@Override
	public String createAuthToken(String userName, Collection<GrantedAuthority> collection) {
		//construction du Json web token
				String jwt = Jwts.builder()
						.setSubject(userName) // ajout de username
						.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME_AUTH_TOKEN)) // ajout d'une date d'expiration
						.signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET) // partie secrete servant de clé, avec un algorithme de type HS 256
						.claim(SecurityConstants.AUTHORITIES_PREFIX, collection) // ajout personnalisé --> on ajoute les roles
						.compact(); // construction du token
		return jwt;
	}

	@Override
	public String createRefreshToken(String userName, Collection<GrantedAuthority> collection) {
					
		//construction du Json web token
		String jwt = Jwts.builder()
				.setSubject(userName) // ajout de username
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME_REFRESH_TOKEN)) // ajout d'une date d'expiration
				.signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET) // partie secrete servant de clé, avec un algorithme de type HS 256
				.claim(SecurityConstants.AUTHORITIES_PREFIX, collection) // ajout personnalisé --> on ajoute les roles
				.claim(SecurityConstants.REFRESH_ACTIVE_PREFIX, true) // ajout de vérification pour le rafraichissement
				.compact(); // construction du token
		return jwt;
	}

	@Override
	public Claims validateRefreshToken(String token) {
		// on récupère le token dans un object Claims
		Claims claims = Jwts.parser().setSigningKey(SecurityConstants.SECRET) // on assigne le secret
				.parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, "")) // on enlève le préfixe
				.getBody(); // on récupère le corp
		
		// on recupere le contact pour comparaison
		Contact contact= accountService.findContactByUserName(claims.getSubject());
		
		//si le contact est toujours bon, alors le token est toujours valide
		if(contact.isActive() == (boolean) claims.get(SecurityConstants.REFRESH_ACTIVE_PREFIX))
			return claims;
		
		return null;
	}
	
	
	
}
