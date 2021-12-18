package com.littlestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.littlestore.entity.Product;
 
public interface ProductRepository extends CrudRepository<Product, String> {

	@Query(value = "SELECT p FROM Product p WHERE p.name LIKE '%' || :keyword || '%'"
            + " OR p.description LIKE '%' || :keyword || '%'")
    public List<Product> findByName(@Param("keyword") String keyword);
	
	// Get list of Product Main Categories
	@Query(value = "SELECT DISTINCT p.categoryMain FROM product p ORDER BY p.categoryMain", nativeQuery=true)
	public List<String> findAllCategoryMainAsc();
    
	public List<Product> findByCategoryMain(String categoryName);
		
//	public List<Product> findByCategoryMainOrderByDescription(String categoryName);
	
	public List<Product> findByCategoryMainOrderByCategorySpecificAscNameAscOptionsAscSizeAsc(String categoryName);
    
	public List<Product> findByCategoryMainAndStockQtyGreaterThanOrderByCategorySpecificAscNameAscOptionsAscSizeAsc(String categoryName, int minQty);
    
//	public List<String> findAllDistinctCategorySpecificGroupByCategoryMainOrderByCategorySpecific();
	
	// Get list of Product Specific Categories Under the passed Main Category
	@Query(value = "SELECT DISTINCT p.categorySpecific FROM product p "
				+ "WHERE p.categoryMain = :mainCat ORDER BY p.categorySpecific", nativeQuery=true)
	public List<String> findAllCategorySpecificUnderMainAsc(@Param("mainCat") String categoryMain);
    
	public List<Product> findByCategorySpecific(String categoryName);
    
	public List<Product> findByCategorySpecificOrderByNameAscOptionsAscSizeAsc(String subCategoryName);

	public List<Product> findByCategorySpecificAndStockQtyGreaterThanOrderByNameAscOptionsAscSizeAsc(String subCategoryName, int minQty);
}