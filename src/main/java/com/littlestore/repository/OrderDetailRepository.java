package com.littlestore.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.littlestore.entity.Order;
import com.littlestore.entity.OrderDetail;
import com.littlestore.entity.OrderDetailId;
 
public interface OrderDetailRepository extends CrudRepository<OrderDetail, OrderDetailId> {
 
//	@Query(value = "SELECT DISTINCT p.categorySpecific FROM product p "
//			+ "WHERE p.categoryMain = :mainCat ORDER BY p.categorySpecific", nativeQuery=true)
	public List<OrderDetail> findByOrderOrderByLineNumberAsc(Order order);
}