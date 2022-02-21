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
	
	@Query(value = "SELECT p FROM Product p JOIN Customer c ON p.dateAdded > c.lastVisited WHERE c.id = :customerNum ORDER BY p.categoryMain ASC, p.categorySpecific ASC, p.name ASC, p.options ASC")
	public List<Product> getNewItems(@Param("customerNum") int customerNumber);
	
	@Query(value = "SELECT p FROM Product p WHERE p.currentPrice <= 1.00 AND p.stockQty > 0 ORDER BY p.categoryMain ASC, p.categorySpecific ASC, p.name ASC, p.options ASC")
	public List<Product> getDollarItems();
	
	@Query(value = "SELECT p FROM Product p WHERE (p.description LIKE %:searchText% OR p.categoryMain LIKE %:searchText% OR p.categorySpecific LIKE %:searchText%) AND p.stockQty > 0 ORDER BY p.categoryMain ASC, p.categorySpecific ASC, p.name ASC, p.options ASC")
	public List<Product> getSearchResults(String searchText);
	
	// Get list of Product Main Categories
	@Query(value = "SELECT DISTINCT p.categoryMain FROM product p ORDER BY p.categoryMain", nativeQuery=true)
	public List<String> findAllCategoryMainAsc();

	@Query(value = "SELECT DISTINCT p.categoryMain FROM product p WHERE p.stockQty != 0 ORDER BY p.categoryMain", nativeQuery=true)
	public List<String> findAllCategoryMainWithStockAsc();

	public List<Product> findByCategoryMain(String categoryName);
		
//	public List<Product> findByCategoryMainOrderByDescription(String categoryName);
	
	public List<Product> findByCategoryMainOrderByCategorySpecificAscNameAscSizeAscOptionsAsc(String categoryName);
    
	public List<Product> findByCategoryMainAndStockQtyGreaterThanOrderByCategorySpecificAscNameAscSizeAscOptionsAsc(String categoryName, int minQty);
    
//	public List<String> findAllDistinctCategorySpecificGroupByCategoryMainOrderByCategorySpecific();
	
	// Get list of Product Specific Categories under the passed Main Category
	@Query(value = "SELECT DISTINCT p.categorySpecific FROM product p "
				+ "WHERE p.categoryMain = :mainCat ORDER BY p.categorySpecific", nativeQuery=true)
	public List<String> findAllCategorySpecificUnderMainAsc(@Param("mainCat") String categoryMain);

	// Get list of Product Specific Categories that have stock, under the passed Main Category
	@Query(value = "SELECT DISTINCT p.categorySpecific FROM product p WHERE p.stockQty !=0 "
			+ "AND p.categoryMain = :mainCat ORDER BY p.categorySpecific", nativeQuery=true)
	public List<String> findAllCategorySpecificUnderMainWithStockAsc(@Param("mainCat") String categoryMain);

	public List<Product> findByCategorySpecific(String categoryName);
    
	public List<Product> findByCategorySpecificOrderByNameAscSizeAscOptionsAsc(String subCategoryName);

	public List<Product> findByCategorySpecificAndStockQtyGreaterThanOrderByNameAscSizeAscOptionsAsc(String subCategoryName, int minQty);
}