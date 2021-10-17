package com.littlestore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlestore.entity.Cart;
//import com.littlestore.entity.Customer;
import com.littlestore.repository.CartRepository;

@Service
public class CartService {
	
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CustomerService customerService;

	public CartService(CartRepository cartRepository) {
		this.cartRepository = cartRepository;
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public void save(Cart cart) {	// Create new/Update cart in DB for current user
		cartRepository.save(cart);
	}

	public List<Cart> listAll() {
		return (List<Cart>) cartRepository.findAll();
	}
	 
	public Cart get(Integer id) {
		return cartRepository.findById(id).get();
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer id) {
		cartRepository.deleteById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete(Cart cart) {
		cartRepository.delete(cart);
	}
	
	public Cart findByCustomerId(int customerId) {
		return cartRepository.findByCustomer(customerService.findById(customerId));
	}
	
	public Cart findByCustomerEmail(String email) {
		return cartRepository.findByCustomer(customerService.findByEmail(email));
	}
}