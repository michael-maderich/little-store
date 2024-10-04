package com.littlestore.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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

	public List<Product> getNewItems(int customerNumber) {
		return repo.getNewItems(customerNumber);
	}
	
	public List<Product> getDollarItems() {
		return repo.getDollarItems();
	}
	
	public List<Product> getSaleItems() {
		return repo.getSaleItems();
	}
	
	public List<Product> getSearchResults(String searchText) {
		return repo.getSearchResults(searchText);
	}

	public List<Product> getSearchResultsWithStock(String searchText) {
		return repo.getSearchResultsWithStock(searchText);
	}

    public Stream<Product> findProductsContainingDesc(Collection<String> withDesc) {
        Specification<Product> specifications = null;
        for (String s : withDesc) {
            if(specifications == null){
                specifications = hasDescriptionLike(s);
            }else{
                specifications = specifications.or(hasDescriptionLike(s));
            }
        }
        return repo.findAll(specifications).stream();
    }

    public static Specification<Product> hasDescriptionLike(String desc) {
        return (root, query, builder) -> builder.like(root.get("description"), "%" + desc + "%");
    }
    
	public List<String> listCategoryMain() {
		return repo.findAllCategoryMainAsc();
	}	 
	
	public List<String> listCategoryMainWithStock() {
		return repo.findAllCategoryMainWithStockAsc();
	}	 
	
	public List<Product> findByCategoryMain(String categoryName) {
		return repo.findByCategoryMain(categoryName);
	}
	
	public List<Product> findByCategoryMainSorted(String categoryName) {
		return repo.findByCategoryMainOrderByCategorySpecificAscNameAscSizeDescOptionsAsc(categoryName);
//		return repo.findByCategoryMainOrderByDescription(categoryName);
	}

	public List<Product> findByCategoryMainMinQtySorted(String categoryName, int minQty) {
		return repo.findByCategoryMainAndStockQtyGreaterThanOrderByCategorySpecificAscNameAscSizeDescOptionsAsc(categoryName, minQty);
	}
    
	public List<String> listCategorySpecificUnderMain(String categoryMain) {
		return repo.findAllCategorySpecificUnderMainAsc(categoryMain);
	}	 
	
	public List<String> listCategorySpecificUnderMainWithStock(String categoryMain) {
		return repo.findAllCategorySpecificUnderMainWithStockAsc(categoryMain);
	}	 
	
	public List<Product> findByCategorySpecific(String categoryName) {
		return repo.findByCategorySpecific(categoryName);
	}
	
	public List<Product> findByCategorySpecificSorted(String subCategoryName) {
		return repo.findByCategorySpecificOrderByNameAscSizeDescOptionsAsc(subCategoryName);		
	}
	
	public List<Product> findByCategorySpecificMinQtySorted(String subCategoryName, int minQty) {
		return repo.findByCategorySpecificAndStockQtyGreaterThanOrderByNameAscSizeDescOptionsAsc(subCategoryName, minQty);
	}
	
	public List<ArrayList<ArrayList<ArrayList<Product>>>> findAllByCatAndSubcat() {
		List<String> catList =  listCategoryMainWithStock();
		List<ArrayList<ArrayList<ArrayList<Product>>>> listByCatAndSubcat = new ArrayList<ArrayList<ArrayList<ArrayList<Product>>>>();
		for (String cat : catList) {
			List<String> subcatList = listCategorySpecificUnderMainWithStock(cat);
			ArrayList<ArrayList<ArrayList<Product>>> listBySubcats = new ArrayList<ArrayList<ArrayList<Product>>>();
			for (String subcat : subcatList) {
				ArrayList<ArrayList<Product>> listByNameAndSize = new ArrayList<ArrayList<Product>>();
				ArrayList<Product> groupItems = new ArrayList<Product>();
				ArrayList<Product> subCatItems = (ArrayList<Product>) findByCategorySpecificMinQtySorted(subcat, 0);
				String name = subCatItems.get(0).getName();
				String size = subCatItems.get(0).getSize();
				for (Product p : subCatItems) {
					if (p.getName().equals(name) && p.getSize().equals(size)) {
						groupItems.add(p);
					}
					else {
						listByNameAndSize.add(groupItems);
						name = p.getName();
						size = p.getSize();
						groupItems = new ArrayList<Product>();
						groupItems.add(p);
					}
				}
				listByNameAndSize.add(groupItems);
				listBySubcats.add(listByNameAndSize);
			}
			listByCatAndSubcat.add(listBySubcats);
		}
		return listByCatAndSubcat;
	}
	
	public List<String> getTransparentImages() {
		return repo.findTransparentImages();
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