package com.test.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.test.entities.Product;

//crossOrigin permet à toute demande HTTP provenant d'une url différente à 
//celle de l'application d'optenir les informations demandé
//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
//@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, Long> {

	
	// LE TEST
	// http://localhost:8080/<base-path>products/search/byNomPage?mc=Télé&page=0&size=1
	//@RestResource(path = "/byNomPage")
	public Page<Product> findByNameContains(@Param("mc") String name, Pageable pageable);

	// RAPPEL SANS UTILISER DE METHODE AUTOMATIQUE DE SPRING DATA
	// http://localhost:8080/<base-path>products/search/searchByNom?mc=
	//@RestResource(path = "/searchByName")
	@Query("select p from Product p where p.name like %:mc%")
	public Page<Product> search(@Param("mc") String name, Pageable pageable);

	// LE TEST
	// http://localhost:8080/<base-path>products/search/bySelected
	//@RestResource(path = "/bySelected")
	public List<Product> findBySelectedIsTrue();

	//@RestResource(path = "/byPromotion")
	public Page<Product> findByPromotionIsTrue(Pageable pageable);
	
	
	@Query("select p from Product p where p.category.id = :id")
	public Page<Product> findByCategoryId(@Param("id") Long id, Pageable pageable);
}
