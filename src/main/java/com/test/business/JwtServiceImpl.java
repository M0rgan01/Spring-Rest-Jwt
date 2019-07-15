package com.test.business;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.test.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@Service
public class JwtServiceImpl implements JwtService{

	@Override
	public Collection<GrantedAuthority> getListAuthorities(Claims claims) {
				//on recupere la liste des roles
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, String>> roles = (ArrayList<Map<String, String>>) claims.get("roles");
				
				//on l'assigne a une collection spring d'authorities
				Collection<GrantedAuthority> authorities = new ArrayList<>();
				roles.forEach(r->{
			authorities.add(new SimpleGrantedAuthority(r.get("authority")));
				});
								
		return authorities;
	}

	@Override
	public String createToken(String userName, Collection<GrantedAuthority> list) {
		//construction du Json web token
				String jwt = Jwts.builder()
						.setSubject(userName) // ajout de username
						.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)) // ajout d'une date d'expiration
						.signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET) // partie secrete servant de clé, avec un algorithme de type HS 256
						.claim(SecurityConstants.AUTHORITIES_PREFIX, list) // ajout personnalisé --> on ajoute les roles
						.compact(); // construction du token
		return jwt;
	}

	
	
}
