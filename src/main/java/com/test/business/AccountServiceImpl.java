package com.test.business;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
import com.test.utilities.ValidatorUtilities;



/**
 * 
 * @author pichat morgan
 * 
 * Classe service de gestion de contact
 *
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService, UserDetailsService{

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private ValidatorUtilities<ContactDTO> validatorUtilities;
	
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
		Contact contact = contactRepository.findByUserName(username);
		
		contact.getRoles().add(role);
		return contact;
	}

	@Override
	public Contact findContactByUserName(String username) {		
		return contactRepository.findByUserName(username);
	}

	
	public void validateContact(ContactDTO contactDTO) throws BusinessException {
		
		validatorUtilities.validate(contactDTO);

		if (!contactDTO.getPassword().equals(contactDTO.getConfirm()))
			throw new BusinessException("mauvaise confirmation du mot de passe");

		Contact testExist = findContactByUserName(contactDTO.getUsername());

		if (testExist != null)
			throw new BusinessException("This user already exists");
			
	}
	
	
	
	
	
	
	
	//Classe de chargement d'un utilisateur pour l'authentification
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Contact contact = findContactByUserName(username);
							
		if (contact == null)
			throw new UsernameNotFoundException(username);

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		contact.getRoles().forEach(r -> {
			authorities.add(new SimpleGrantedAuthority(r.getRole()));
		});
				
		return new User(contact.getUserName(), contact.getPassWord(), authorities);
	}

}
