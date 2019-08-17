package com.test.entities;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Contact {

	///////////////////// ATTRIBUTS ///////////////////////

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true) // n'autorise pas 2 userName identique
	@NotBlank(message = "contact.username.blank")
	@Size(min = 4, max = 15, message = "contact.username.size.not.correct")
	private String userName;
	@Temporal(TemporalType.DATE) // --> renseigne uniquement la DATE 01/01/1991
	private Date dateNaissance;	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$", message = "contact.password.not.true")
	private String passWord;
	@Transient
	private String confirm;
	@Transient
	private String oldPassWord;
	private String tel;
	private String photo;
	private boolean active;		
	private int tryConnection; // Nombre d'échec de connection	
	private Date expiryConnection; // Date d'expiration lors d'un nombre d'échec trop grand
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Roles> roles = new HashSet<>();
	@Valid
	@OneToOne(cascade = CascadeType.ALL)
	private Mail mail;
	
	///////////////////// CONSTRUCTEURS ///////////////////////

	public Contact(String userName, Date dateNaissance, Mail mail, String passWord, String tel, String photo,
			boolean active) {
		super();
		this.userName = userName;
		this.dateNaissance = dateNaissance;
		this.mail = mail;
		this.passWord = passWord;
		this.tel = tel;
		this.photo = photo;
		this.active = active;
	}

	public Contact() {
		super();
	}

	///////////////////// GUETTERS / SETTERS ///////////////////////

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

	@JsonIgnore // n'affiche par le passWord en reponse JSON
	public String getPassWord() {
		return passWord;
	}

	@JsonProperty // permet de récupéré la propriété en requete
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	@JsonIgnore
	public String getConfirm() {
		return confirm;
	}

	@JsonProperty
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	
	@JsonIgnore
	public String getOldPassWord() {
		return oldPassWord;
	}
	
	@JsonProperty
	public void setOldPassWord(String oldPassWord) {
		this.oldPassWord = oldPassWord;
	}
	
	public Collection<Roles> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Roles> roles) {
		this.roles = roles;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getTryConnection() {
		return tryConnection;
	}

	public void setTryConnection(int tryConnection) {
		this.tryConnection = tryConnection;
	}

	public Date getExpiryConnection() {
		return expiryConnection;
	}

	public void setExpiryConnection(Date expiryConnection) {
		this.expiryConnection = expiryConnection;
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

	
	
}
