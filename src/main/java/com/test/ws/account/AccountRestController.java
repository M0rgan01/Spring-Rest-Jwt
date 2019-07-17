package com.test.ws.account;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.test.business.AccountService;
import com.test.business.JwtService;
import com.test.entities.Contact;
import com.test.entities.ContactDTO;
import com.test.entities.Roles;
import com.test.exception.BusinessException;
import com.test.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

/**
 * 
 * Service web pour la partie auhtentification utilisateur
 * 
 * @author pichat morgan
 *
 */
@RestController
public class AccountRestController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private JwtService jwtService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody ContactDTO contactDTO) throws BusinessException {

		try {
			accountService.validateContact(contactDTO);
		} catch (BusinessException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
		}

		Contact contact = new Contact();
		contact.setUserName(contactDTO.getUsername());
		contact.setPassWord(contactDTO.getPassword());
		contact.setActive(true);
		
		accountService.saveContact(contact);
		contact = accountService.addRoleToContact(contactDTO.getUsername(), "ROLE_USER");

		Collection<GrantedAuthority> collection = new ArrayList<GrantedAuthority>();

		for (Roles role : contact.getRoles()) {
			collection.add(new SimpleGrantedAuthority(role.getRole()));
		}

		// on créer un token JWT
		String jwt = jwtService.createAuthToken(contact.getUserName(), collection);
		String jwtRefresh = jwtService.createRefreshToken(contact.getUserName(), collection);

		// on l'ajoute au headers de la réponse
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(SecurityConstants.HEADER_AUTH_STRING, SecurityConstants.TOKEN_PREFIX + jwt);
		responseHeaders.add(SecurityConstants.HEADER_REFRESH_STRING, SecurityConstants.TOKEN_PREFIX + jwtRefresh);

		return new ResponseEntity<Contact>(contact, responseHeaders, HttpStatus.OK);
	}

	@PostMapping(value = "/token-refresh")
	public ResponseEntity<?> refreshToken(@RequestBody String tokenRefresh) {
		
		try {
			Claims claims = jwtService.validateRefreshToken(tokenRefresh);

			// on créer un token JWT
			String jwt = jwtService.createAuthToken(claims.getSubject(), jwtService.getListAuthorities(claims));

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add(SecurityConstants.HEADER_AUTH_STRING, SecurityConstants.TOKEN_PREFIX + jwt);
			responseHeaders.add(SecurityConstants.HEADER_REFRESH_STRING, SecurityConstants.TOKEN_PREFIX + tokenRefresh);
			
			return new ResponseEntity<String>(null ,responseHeaders, HttpStatus.OK);

		} catch (ExpiredJwtException e) {
			return new ResponseEntity<String>("error.jwt.expired", HttpStatus.UNAUTHORIZED);
		} catch (JwtException e) {
			return new ResponseEntity<String>("error.jwt.invalid", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
		}
	}
}
