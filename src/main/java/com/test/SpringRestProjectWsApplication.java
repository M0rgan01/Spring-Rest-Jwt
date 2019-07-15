package com.test;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.business.AccountService;
import com.test.dao.ContactRepository;
import com.test.dao.TaskRepository;
import com.test.entities.Contact;
import com.test.entities.Roles;
import com.test.entities.Task;
import com.test.security.JWTAuthenticationFilter;
import com.test.security.JWTAuthorizationFilter;

@SpringBootApplication
public class SpringRestProjectWsApplication implements CommandLineRunner{

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private AccountService accountService;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringRestProjectWsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//contactRepository.save(new Contact("pichat","morgan", new Date(), "pichat.morgan@gmail.com","0474000000","test"));
//		taskRepository.save(new Task("Tache 1"));
//		taskRepository.save(new Task("Tache 2"));
//		taskRepository.save(new Task("Tache 3"));
//		
//		
//		taskRepository.findAll().forEach(t->{
//			System.out.println(t.getTaskName());
//		});
//		accountService.saveContact(new Contact("admin", new Date(), "admin@gmail.com", "1234", "1234", "1452"));
//		accountService.saveContact(new Contact("user", new Date(), "admin@gmail.com", "1234", "1234", "1452"));
//		accountService.saveRole(new Roles("ROLE_ADMIN"));
//		accountService.saveRole(new Roles("ROLE_USER"));
//		accountService.addRoleToContact("admin", "ROLE_ADMIN");
//		accountService.addRoleToContact("admin", "ROLE_USER");
//		accountService.addRoleToContact("user", "ROLE_USER");
	
	}

}
