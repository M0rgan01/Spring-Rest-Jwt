package com.test.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.dao.ContactRepository;
import com.test.dao.RoleRepository;
import com.test.entities.Contact;
import com.test.entities.Roles;
import com.test.exception.BusinessException;

/**
 * 
 * @author pichat morgan
 * 
 *         Classe service de gestion de contact
 *
 */
@Service
@Transactional
public class ContactServiceImpl implements ContactService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public Contact saveContact(Contact c) {
		String hashPW = bCryptPasswordEncoder.encode(c.getPassWord());
		c.setPassWord(hashPW);
		return contactRepository.save(c);
	}

	@Override
	public Roles saveRole(Roles r) {
		return roleRepository.save(r);
	}

	@Override
	public Contact addRoleToContact(String username, String roleName) {
		Roles role = roleRepository.findRolesByRole(roleName);
		Contact contact = contactRepository.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		contact.getRoles().add(role);
		return contact;
	}

	@Override
	public Optional<Contact> findContactByUserName(String username) {
		return contactRepository.findByUserName(username);
	}

	@Override
	public Optional<Contact> findContactByEmail(String email) {		
		return contactRepository.findByEmail(email);
	}
	
	public void validateCreateContact(Contact contact) throws BusinessException {

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

		for (ConstraintViolation<Contact> violation : violations) {
			throw new BusinessException(violation.getMessage());
		}

		if (!contact.getPassWord().equals(contact.getConfirm()))
			throw new BusinessException("mauvaise confirmation du mot de passe");

		Contact testExist = findContactByUserName(contact.getUserName()).orElse(null);

		if (testExist != null)
			throw new BusinessException("This user already exists");
	}

	// Convertit une liste de role en liste de GrantedAuthority
	public List<GrantedAuthority> getAuthorities(Collection<Roles> roles) {

		if (roles == null || roles.isEmpty())
			throw new IllegalArgumentException("list.roles.empty");

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (Roles role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getRole()));
		}
		return authorities;
	}

	public String getPrincipal(Contact contact) {

		// vérification de la validité de l'object
		if (contact.getUserName() != null && !contact.getUserName().isEmpty()) {
			return contact.getUserName();
		
		} else if (contact.getMail().getEmail() != null && !contact.getMail().getEmail().isEmpty()) {
			return contact.getMail().getEmail();
		
		} else {
			throw new AuthenticationServiceException("empty.mail.or.username");
		}

	}

}
