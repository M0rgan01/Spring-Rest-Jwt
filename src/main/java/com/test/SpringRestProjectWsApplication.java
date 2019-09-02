package com.test;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.business.ContactService;
import com.test.dao.CategoryRepository;
import com.test.dao.ContactRepository;
import com.test.dao.ProductRepository;
import com.test.entities.Category;
import com.test.entities.Contact;
import com.test.entities.Mail;
import com.test.entities.Product;
import com.test.entities.Roles;

import net.bytebuddy.utility.RandomString;

@SpringBootApplication
public class SpringRestProjectWsApplication implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(SpringRestProjectWsApplication.class);

	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private ProductRepository produitRepository;
	@Autowired
	private CategoryRepository CategorieRepository;
	@Autowired
	private ContactService contactService;
	@Autowired
	private RepositoryRestConfiguration repositoryRestConfiguration;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringRestProjectWsApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder getBCPE() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ObjectMapper getOM() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper;
	}

	@Override
	public void run(String... args) throws Exception {

		// permet d'afficher les IDs dans le JSON
		repositoryRestConfiguration.exposeIdsFor(Product.class, Category.class);

		CategorieRepository.save(new Category("Ordinateur", null, null, null));
		CategorieRepository.save(new Category("Imprimante", null, null, null));
		CategorieRepository.save(new Category("Téléphone", null, null, null));

		Random rm = new Random();

		CategorieRepository.findAll().forEach(c -> {
			for (int i = 0; i < 10; i++) {
				Product p = new Product();
				p.setName(RandomString.make(18));
				p.setCurrentPrice(100 + rm.nextInt(1000));
				p.setStock(rm.nextInt(50));
				p.setAvailable(rm.nextBoolean());
				p.setSelected(rm.nextBoolean());
				p.setPromotion(rm.nextBoolean());
				p.setPhoto("angular.png");
				p.setCategory(c);
				produitRepository.save(p);
			}
		});
		
		
		
		Mail mail = new Mail("admin@gmail.com");
		Mail mail2 = new Mail("user@gmail.com");
		
		Contact c = new Contact("admin", new Date(), mail, "Test1234", "Test1234", "1452", true);
		c.setConfirm("Test1234");
		Contact c2 = new Contact("user", new Date(), mail2, "Test1234", "Test1234", "1452", true);
		c2.setConfirm("Test1234");
		contactService.createContact(c);
		contactService.createContact(c2);
		contactService.saveRole(new Roles("ROLE_ADMIN"));
		contactService.saveRole(new Roles("ROLE_USER"));
		contactService.addRoleToContact("admin", "ROLE_ADMIN");
		contactService.addRoleToContact("admin", "ROLE_USER");
		contactService.addRoleToContact("user", "ROLE_USER");

	}

}
