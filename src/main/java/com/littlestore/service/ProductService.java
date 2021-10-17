package com.littlestore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlestore.entity.Product;
//import com.littlestore.pagination.PaginationResult;
import com.littlestore.repository.ProductRepository;

@Service
//@Transactional
public class ProductService {
	
	ProductRepository repo;
	@Autowired
	public ProductService(ProductRepository repo) {
		this.repo = repo;
	}

	@Transactional(rollbackFor = Exception.class)
	public void save(Product product) {	// Create/Update
		repo.save(product);
	}
	 
	public List<Product> listAll() {
		return (List<Product>) repo.findAll();
	}
	 
	@Transactional(rollbackFor = Exception.class)
	public void delete(String upc) {
		repo.deleteById(upc);
	}

	public Product get(String upc) {
		return repo.findById(upc).get();
	}

	public List<Product> findByName(String name) {
		return repo.findByName(name);
	}

	public List<String> listCategoryMain() {
		return repo.findAllCategoryMainAsc();
	}	 
	
	public List<Product> findByCategoryMain(String categoryName) {
		return repo.findByCategoryMain(categoryName);
	}
	
	public List<Product> findByCategoryMainSorted(String categoryName) {
		return repo.findByCategoryMainOrderByCategorySpecificAscNameAscOptionsAscSizeAsc(categoryName);
//		return repo.findByCategoryMainOrderByDescription(categoryName);
	}

	public List<String> listCategorySpecificUnderMain(String categoryMain) {
		return repo.findAllCategorySpecificUnderMainAsc(categoryMain);
	}	 
	
	public List<Product> findByCategorySpecific(String categoryName) {
		return repo.findByCategorySpecific(categoryName);
	}
	
	public List<Product> findByCategorySpecificSorted(String subCategoryName) {
		return repo.findByCategorySpecificOrderByNameAscOptionsAscSizeAsc(subCategoryName);		
	}
	
/*	public PaginationResult<Product> queryProducts(int page, int maxResult, int maxNavigationPage, String likeName) {
		String sql = "Select new " + Product.class.getName() //
				+ "(p.code, p.name, p.price) " + " from "//
				+ Product.class.getName() + " p ";
		if (likeName != null && likeName.length() > 0) {
			sql += " Where lower(p.name) like :likeName ";
		}
		sql += " order by p.createDate desc ";
		// 
		Session session = this.sessionFactory.getCurrentSession();
		Query<Product> query = session.createQuery(sql, Product.class);
 
		if (likeName != null && likeName.length() > 0) {
			query.setParameter("likeName", "%" + likeName.toLowerCase() + "%");
		}
		return new PaginationResult<Product>(query, page, maxResult, maxNavigationPage);
	}
 
	public PaginationResult<Product> queryProducts(int page, int maxResult, int maxNavigationPage) {
		return queryProducts(page, maxResult, maxNavigationPage, null);
	}*/
}