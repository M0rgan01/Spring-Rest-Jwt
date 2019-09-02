package com.test.business;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
@PropertySource("classpath:authentication.properties")
public class ContactServiceImpl implements ContactService {

	@Value("${max.try.incorrect.login}")
	private int tryIncorrectLogin;
	@Value("${waiting.minutes.for.max.try.login}")
	private int waitingMinutes;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public Contact createContact(Contact contact) throws BusinessException {
		validateCreateContact(contact);
		contact.setActive(true);
		String hashPW = bCryptPasswordEncoder.encode(contact.getPassWord());
		contact.setPassWord(hashPW);
		return contactRepository.save(contact);
	}

	@Override
	public Roles saveRole(Roles r) {
		return roleRepository.save(r);
	}

	@Override
	public Contact addRoleToContact(String username, String roleName) {
		Roles role = roleRepository.findRolesByRole(roleName);
		Contact contact = contactRepository.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("contact.not.found"));

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

	@Override
	public void validateCreateContact(Contact contact) throws BusinessException {

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Contact>> violations = validator.validate(contact);

		for (ConstraintViolation<Contact> violation : violations) {
			throw new BusinessException(violation.getMessage());
		}

		if (!contact.getPassWord().equals(contact.getConfirm()))
			throw new BusinessException("contact.incorrect.password.confirm");

		Contact testExistUsername = findContactByUserName(contact.getUserName()).orElse(null);

		if (testExistUsername != null)
			throw new BusinessException("contact.username.already.exist");

		Contact testExistEmail = findContactByEmail(contact.getMail().getEmail()).orElse(null);

		if (testExistEmail != null)
			throw new BusinessException("contact.email.already.exist");

	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void incorrectPassworld(Contact contact) {

		contact.setTryConnection(contact.getTryConnection() + 1);

		if (contact.getTryConnection() >= tryIncorrectLogin) {

			Calendar date = Calendar.getInstance();
			date.add(Calendar.MINUTE, waitingMinutes);
			contact.setExpiryConnection(date.getTime());

			contactRepository.save(contact);
			throw new AuthenticationServiceException("contact.tryConnection.out");
		}
		contactRepository.save(contact);
	}

	@Override
	public void checkPermissionToLogin(Contact contact) {

		if (!contact.isActive())
			throw new AuthenticationServiceException("contact.not.active");

		if (contact.getExpiryConnection() != null) {
			if (contact.getExpiryConnection().after(new Date())) {
				throw new AuthenticationServiceException("contact.expiryConnection.after.date");
			} else {
				contact.setTryConnection(0);
				contact.setExpiryConnection(null);
				contactRepository.save(contact);
			}
		}
	}

}
