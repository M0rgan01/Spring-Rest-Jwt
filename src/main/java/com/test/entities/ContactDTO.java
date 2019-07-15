package com.test.entities;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Object servant d'information sur le formulaire d'inscritpion
 * 
 * @author pichat morgan
 *
 */

public class ContactDTO {
	@NotBlank(message="contact.username.blank")
	@Size(min = 5, max = 15, message = "contact.username.size.not.correct")
	private String username;
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$", message = "contact.password.not.true")
	private String password;
	private String confirm;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	
	
}
