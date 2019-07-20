package com.test.business;

import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.dao.ContactRepository;
import com.test.dao.RoleRepository;
import com.test.entities.Contact;
import com.test.entities.ContactDTO;
import com.test.entities.Roles;
import com.test.exception.BusinessException;



/**
 * 
 * @author pichat morgan
 * 
 * Classe service de gestion de contact
 *
 */
@Service
@Transactional
public class ContactServiceImpl implements ContactService{

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
		Contact contact = contactRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		
		contact.getRoles().add(role);
		return contact;
	}

	@Override
	public Optional<Contact> findContactByUserName(String username) {		
		return contactRepository.findByUserName(username);
	}

	
	public void validateCreateContact(ContactDTO contactDTO) throws BusinessException {
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<ContactDTO>> violations = validator.validate(contactDTO);
		
		for (ConstraintViolation<ContactDTO> violation : violations) {		    
		    throw new BusinessException(violation.getMessage());
		}

		if (!contactDTO.getPassword().equals(contactDTO.getConfirm()))
			throw new BusinessException("mauvaise confirmation du mot de passe");
		
		
		Contact testExist = findContactByUserName(contactDTO.getUsername()).orElse(null);
		
		if (testExist != null)
			throw new BusinessException("This user already exists");			
	}
	
}
