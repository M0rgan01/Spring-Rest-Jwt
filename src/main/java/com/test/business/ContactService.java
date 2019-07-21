package com.test.business;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;

import com.test.entities.Contact;
import com.test.entities.Roles;
import com.test.exception.BusinessException;

public interface ContactService {
	public Contact saveContact(Contact c);
	public Roles saveRole(Roles r);
	public Contact addRoleToContact(String username, String roleName);
	public Optional<Contact> findContactByUserName(String username);
	public Optional<Contact> findContactByEmail(String email);
	public void validateCreateContact(Contact contact) throws BusinessException;
	public List<GrantedAuthority> getAuthorities(Collection<Roles> roles);
	public String getPrincipal(Contact contact);
}
