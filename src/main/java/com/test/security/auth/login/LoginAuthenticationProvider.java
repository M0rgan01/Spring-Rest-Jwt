package com.test.security.auth.login;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.test.business.ContactService;
import com.test.entities.Contact;
import com.test.security.auth.model.UserContext;

/**
 * Processus d'authentification par login
 * 
 * @author Pichat morgan
 *
 *         20 Juillet 2019
 */
@Component
public class LoginAuthenticationProvider implements AuthenticationProvider {
	private final BCryptPasswordEncoder encoder;
	private final ContactService contactService;

	@Autowired
	public LoginAuthenticationProvider(final ContactService contactService, final BCryptPasswordEncoder encoder) {
		this.contactService = contactService;
		this.encoder = encoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		// vérification de l'objet authentication
		Assert.notNull(authentication, "No authentication data provided");

		// récupération des information de connection
		String username = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();
		
		// récupération du contact pour la comparaison
		Contact contact = contactService.findContactByUserName(username)
				.orElseGet(() ->contactService.findContactByEmail(username)
						.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));
							
		// vérification du password
		if (!encoder.matches(password, contact.getPassWord())) {
			throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
		}

		// vérification des roles
		if (contact.getRoles() == null)
			throw new InsufficientAuthenticationException("User has no roles assigned");

		// création d'une liste d'authority
		List<GrantedAuthority> authorities = contact.getRoles().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toList());

		UserContext userContext = UserContext.create(contact.getUserName(), authorities);

		return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
