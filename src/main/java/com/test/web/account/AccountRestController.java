package com.test.web.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.test.business.ContactService;
import com.test.entities.Contact;
import com.test.entities.Roles;
import com.test.exception.BusinessException;
import com.test.security.SecurityConstants;
import com.test.security.auth.model.UserContext;
import com.test.security.response.ErrorCode;
import com.test.security.response.ErrorResponse;
import com.test.security.token.JwtService;
import com.test.security.token.JwtToken;

import io.jsonwebtoken.ExpiredJwtException;

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
	private ContactService accountService;
	@Autowired
	private JwtService jwtService;
		
	@PostMapping(value = "/api/auth/register")
	public ResponseEntity<?> register(@RequestBody Contact contact) throws BusinessException {

		try {								
			accountService.createContact(contact);
			contact = accountService.addRoleToContact(contact.getUserName(), "ROLE_USER");
		
			// on créer un token JWT
			String jwt = jwtService.createAuthToken(UserContext.create(contact.getUserName(), Roles.getListAuthorities(contact.getRoles())));
			String jwtRefresh = jwtService.createRefreshToken(UserContext.create(contact.getUserName(), null));

			// on l'ajoute au headers de la réponse
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add(SecurityConstants.HEADER_AUTH_STRING, SecurityConstants.TOKEN_PREFIX + jwt);
			responseHeaders.add(SecurityConstants.HEADER_REFRESH_STRING, SecurityConstants.TOKEN_PREFIX + jwtRefresh);

			return new ResponseEntity<Contact>(contact, responseHeaders, HttpStatus.OK);

		} catch (BusinessException e) {
			return new ResponseEntity<ErrorResponse>(
					ErrorResponse.of(e.getMessage(), ErrorCode.REGISTRATION, HttpStatus.NOT_ACCEPTABLE),
					HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {
			return new ResponseEntity<ErrorResponse>(					
					ErrorResponse.of("Une erreur est survenue", ErrorCode.GLOBAL, HttpStatus.INTERNAL_SERVER_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/api/auth/token")
	public ResponseEntity<?> refreshToken(@RequestHeader(SecurityConstants.HEADER_REFRESH_STRING) String tokenRefresh) {

		try {	
			JwtToken token = new JwtToken(jwtService.extract(tokenRefresh));
								
			// on créer un token JWT, grace à la vérification du tokenRefresh
			String jwt = jwtService.createAuthToken(jwtService.validateRefreshToken(token));
								
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add(SecurityConstants.HEADER_AUTH_STRING, SecurityConstants.TOKEN_PREFIX + jwt);
			responseHeaders.add(SecurityConstants.HEADER_REFRESH_STRING, tokenRefresh);
					
			return new ResponseEntity<String>(null, responseHeaders, HttpStatus.OK);

		} catch (ExpiredJwtException e) {
			return new ResponseEntity<ErrorResponse>(
					ErrorResponse.of(e.getMessage(), ErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.NOT_ACCEPTABLE),
					HttpStatus.NOT_ACCEPTABLE);
		} catch (BadCredentialsException e) {					
			return new ResponseEntity<ErrorResponse>(
					ErrorResponse.of(e.getMessage(), ErrorCode.JWT_TOKEN_INVALID, HttpStatus.NOT_ACCEPTABLE),
					HttpStatus.NOT_ACCEPTABLE);
		} catch (Exception e) {		
			return new ResponseEntity<ErrorResponse>(
					ErrorResponse.of("Une erreur est survenue", ErrorCode.GLOBAL, HttpStatus.INTERNAL_SERVER_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
}
