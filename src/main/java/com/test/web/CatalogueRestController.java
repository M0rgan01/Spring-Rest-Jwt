package com.test.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.test.dao.CategoryRepository;
import com.test.dao.ProductRepository;
import com.test.entities.Category;
import com.test.entities.Product;
import com.test.security.response.ErrorCode;
import com.test.security.response.ErrorResponse;

//Spring data permet déjà de réaliser tous ça, 
//mais pour certain traitement un peux tordu, il faudra créer son propre restController

@RestController
public class CatalogueRestController {

	@Autowired
	private ProductRepository produitRepository;
	@Autowired
	private CategoryRepository categoryRepository;

/////////////////// Product //////////////////////////

	@GetMapping(value = "/api/store/pageProducts/{page}/{size}")
	public Page<Product> getPageProducts(@PathVariable(name = "page") int page, @PathVariable(name = "size") int size) {
		return produitRepository.findAll(PageRequest.of(page, size));
	}

	@GetMapping(value = "/api/store/products/byPromotion/{page}/{size}")
	public Page<Product> getListProductsByPromotion(@PathVariable(name = "page") int page,
			@PathVariable(name = "size") int size) {
		return produitRepository.findByPromotionIsTrue(PageRequest.of(page, size));
	}

	@GetMapping(value = "/api/store/products/category/{id}/{page}/{size}")
	public ResponseEntity<?> getListProductsByCategoryId(@PathVariable(name = "id") Long id,
			@PathVariable(name = "page") int page, @PathVariable(name = "size") int size) {

		Page<Product> pageProduct;
		
		try {
			pageProduct = produitRepository.findByCategoryId(id, PageRequest.of(page, size));
		} catch (Exception e) {
			return new ResponseEntity<ErrorResponse>(ErrorResponse.of(e.getMessage(), ErrorCode.GLOBAL, HttpStatus.NOT_ACCEPTABLE), HttpStatus.NOT_ACCEPTABLE);
		}
		
		return new ResponseEntity<Page<Product>>(pageProduct, HttpStatus.OK);
	}

	@GetMapping(value = "/api/store/products/{id}")
	public Product getProductById(@PathVariable(name = "id") Long id) {
		return produitRepository.findById(id).get();
	}

	@DeleteMapping(value = "/listProduits/{id}")
	public void deleteProductById(@PathVariable(name = "id") Long id) {
		produitRepository.deleteById(id);
	}

	@PostMapping(value = "/api/edit/products/photo/{id}")
	public void uploadPhotoProduct(MultipartFile file, @PathVariable(name = "id") Long id) throws IOException {
		Product p = produitRepository.findById(id).orElse(null);
		p.setPhoto(id + ".png");
		Files.write(Paths.get(System.getProperty("user.home") + "/Test/" + p.getPhoto()), file.getBytes());
		produitRepository.save(p);
	}

	@PatchMapping(value = "/api/edit/products")
	public Product updateProduct(@RequestBody Product p) {
		return produitRepository.save(p);
	}

	@PostMapping(value = "/api/edit/products")
	public Product create(@RequestBody Product p) {
		return produitRepository.save(p);
	}

/////////////////// CATEGORY //////////////////////////

	@GetMapping(value = "/api/store/categories")
	public List<Category> getListCategories() {
		return categoryRepository.findAll();
	}

	@GetMapping(value = "/api/store/category/{id}")
	public Category getCategory(@PathVariable(name = "id") Long id) {
		return categoryRepository.findById(id).orElse(null);
	}
	
/////////////////// PHOTO //////////////////////////

	@GetMapping(value = "/api/store/products/photo/{id}", produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getPhoto(@PathVariable(name = "id") Long id) throws IOException {
		Product p = produitRepository.findById(id).orElse(null);
		return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Test/" + p.getPhoto()));
	}

}
