package com.littlestore.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.littlestore.entity.Customer;
import com.littlestore.entity.Order;
 
public interface OrderRepository extends CrudRepository<Order, Integer> {
	public List<Order> findByCustomer(Customer customer);
	
//	@Query(value = "SELECT DISTINCT p.categorySpecific FROM product p "
//			+ "WHERE p.categoryMain = :mainCat ORDER BY p.categorySpecific", nativeQuery=true)
}