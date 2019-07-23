package com.test.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Object representant un Role
 * 
 * @author PICHAT morgan
 *
 */
@Entity
public class Roles{
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true) // n'autorise pas 2 role identique
	private String role;
	
	
	/**
	 * Convertie une liste de String en liste de GrantedAuthority
	 * 
	 * @param roles --> liste de String
	 * @return liste de GrantedAuthority
	 */
	public static List<GrantedAuthority> getListAuthorities(List<String> roles) {

		if (roles == null || roles.isEmpty())
			throw new IllegalArgumentException("contact.list.roles.empty");
		
		List<GrantedAuthority> authorities = roles.stream()
	            .map(SimpleGrantedAuthority::new)
	            .collect(Collectors.toList());
		
		return authorities;
	}
	
	/**
	 * Récupère une liste de GrantedAuthority à partir de claims
	 * 
	 * @param claims --> claims contenant une liste de role
	 * @return liste de GrantedAuthority
	 */
	public static List<GrantedAuthority> getListAuthorities(Collection<Roles> roles) {

		if (roles == null || roles.isEmpty())
			throw new IllegalArgumentException("list.roles.empty");

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (Roles role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getRole()));
		}
		return authorities;
	}
	
	
	public Roles() {
		super();
	}

	public Roles(String role) {
		super();
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public String getAuthority() {
		return role;
		
	}
		
}
