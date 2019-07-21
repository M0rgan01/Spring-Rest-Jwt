package com.test.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;

@Entity
public class Mail {

	
	///////////////////// ATTRIBUTS ///////////////////////
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Email(message = "mail.email.not.true")
	private String email;
	private String tokenRecovery;
	private int tryToken;
	private Date timeForRecovery;
	
	///////////////////// CONSTRUCTEURS ///////////////////////
	
	public Mail() {}

	public Mail(String email) {
		super();
		this.email = email;
	}

	///////////////////// GUETTERS / SETTERS ///////////////////////
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTokenRecovery() {
		return tokenRecovery;
	}

	public void setTokenRecovery(String tokenRecovery) {
		this.tokenRecovery = tokenRecovery;
	}

	public int getTryToken() {
		return tryToken;
	}

	public void setTryToken(int tryToken) {
		this.tryToken = tryToken;
	}

	public Date getTimeForRecovery() {
		return timeForRecovery;
	}

	public void setTimeForRecovery(Date timeForRecovery) {
		this.timeForRecovery = timeForRecovery;
	}
	
	
}
