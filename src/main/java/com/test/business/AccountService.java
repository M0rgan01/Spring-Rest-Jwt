package com.test.business;

import com.test.entities.Contact;
import com.test.entities.ContactDTO;
import com.test.entities.Roles;
import com.test.exception.BusinessException;

public interface AccountService {
	public Contact saveContact(Contact c);
	public Roles saveRole(Roles r);
	public Contact addRoleToContact(String username, String roleName);
	public Contact findContactByUserName(String username);
	public void validateContact(ContactDTO contactDTO) throws BusinessException;
}
