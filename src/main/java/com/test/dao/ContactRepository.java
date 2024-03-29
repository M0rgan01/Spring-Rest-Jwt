package com.test.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.test.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long>{

	@Query("select c from Contact c where c.userName like :x order by c.id")
	public Page<Contact> chercher(@Param("x")String mc, Pageable pageable);
	public Optional<Contact> findByUserName(String username);
	@Query("select c from Contact c where c.mail.email like :x")
	public Optional<Contact> findByEmail(@Param("x")String email);
}
