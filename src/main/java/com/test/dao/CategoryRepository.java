package com.test.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.entities.Category;

//crossOrigin permet à toute demande HTTP provenant d'une url différente à 
//celle de l'application d'optenir les informations demandé
//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
//@RepositoryRestResource
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
