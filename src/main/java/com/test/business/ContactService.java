package com.test.business;

import java.util.Optional;

import com.test.entities.Contact;
import com.test.entities.Roles;
import com.test.exception.BusinessException;

public interface ContactService {
	public Contact saveContact(Contact c);
	public Roles saveRole(Roles r);
	public Contact addRoleToContact(String username, String roleName);
	public Optional<Contact> findContactByUserName(String username);
	public void validateCreateContact(Contact contact) throws BusinessException;
}
