package com.test.entities;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonSetter;

@Entity
public class Contact{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true) // n'autorise pas 2 userName identique
	private String userName;
	@Temporal(TemporalType.DATE) // --> renseigne uniquement la DATE 01/01/1991
	private Date dateNaissance;
	private String email;
	//@JsonIgnore
	private String passWord;
	private String tel;
	private String photo;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Roles> roles = new HashSet<>();
		
	
	public Contact(String userName, Date dateNaissance, String email, String passWord, String tel, String photo) {
		super();
		this.userName = userName;
		this.dateNaissance = dateNaissance;
		this.email = email;
		this.passWord = passWord;
		this.tel = tel;
		this.photo = photo;
	}
	
	public Contact() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getDateNaissance() {
		return dateNaissance;
	}
	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	@JsonSetter //n'affiche par le passWord en reponse JSON
	public String getPassWord() {
		return passWord;
	}
	
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public Collection<Roles> getRoles() {
		return roles;
	}
	public void setRoles(Collection<Roles> roles) {
		this.roles = roles;
	}
	
	
	
	
}
