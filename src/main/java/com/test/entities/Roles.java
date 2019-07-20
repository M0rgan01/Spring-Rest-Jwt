package com.test.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
