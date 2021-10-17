package com.littlestore.repository;

import org.springframework.data.repository.CrudRepository;

import com.littlestore.entity.Cart;
import com.littlestore.entity.Customer;
 
public interface CartRepository extends CrudRepository<Cart, Integer> {

	public Cart findByCustomer(Customer customer);
    
}