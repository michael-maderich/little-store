package com.littlestore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.littlestore.entity.Cart;
import com.littlestore.entity.CartDetail;
import com.littlestore.entity.CartDetailId;
import com.littlestore.entity.Product;
import com.littlestore.repository.CartDetailRepository;

@Service
public class CartDetailService {

	@Autowired
	private CartDetailRepository cartDetailRepository;
	/*
	 * @Autowired private CartService cartService;
	 * 
	 * @Autowired private ProductService productService;
	 */   

	public CartDetailService(CartDetailRepository cartDetailRepository) {
    	this.cartDetailRepository = cartDetailRepository;
    }
	
    
    @Transactional(rollbackFor = Exception.class)
    public void save(CartDetail cartDetail) {	// Create new/Update Cart Detail line item
        cartDetailRepository.save(cartDetail);
    }

    public List<CartDetail> listAllInCart(Cart cart) {
    	return cartDetailRepository.findByCartOrderByLineNumberAsc(cart);
    }
    
    public CartDetail get(CartDetailId id) {
        return cartDetailRepository.findById(id).get();
    }
    // Overloaded method to find a CartDetail (Cart line item) by Cart and Product using any mix of objects and/or IDs for both
	public CartDetail findLineByCartAndProduct(int cartNum, String upc) {
		return get(new CartDetailId(cartNum, upc));
		//return cartDetailRepository.findLineByCartNumAndUpc(cartNum, upc);
	}
	public CartDetail findLineByCartAndProduct(Cart cart, String upc) {
		return get(new CartDetailId(cart.getCartId(), upc));
		//return cartDetailRepository.findLineByCartNumAndUpc(cart.getCartId(), upc);
	}
	public CartDetail findLineByCartAndProduct(int cartNum, Product product) {
		return get(new CartDetailId(cartNum, product.getUpc()));
		//return cartDetailRepository.findLineByCartNumAndUpc(cartNum, product.getUpc());
	}
	public CartDetail findLineByCartAndProduct(Cart cart, Product product) {
		return get(new CartDetailId(cart.getCartId(), product.getUpc()));
		//return cartDetailRepository.findLineByCartNumAndUpc(cart.getCartId(), product.getUpc());
	}
	
    @Transactional(rollbackFor = Exception.class)
    public void delete(CartDetailId id) {
        cartDetailRepository.deleteById(id);
    }
    // Overloaded method to find a CartDetail (Cart line item) by Cart and Product using any mix of objects and/or IDs for both
	public void deleteLineByCartAndProduct(int cartNum, String upc) {
		delete(new CartDetailId(cartNum, upc));
	}
	public void deleteLineByCartAndProduct(Cart cart, String upc) {
		delete(new CartDetailId(cart.getCartId(), upc));
	}
	public void deleteLineByCartAndProduct(int cartNum, Product product) {
		delete(new CartDetailId(cartNum, product.getUpc()));
	}
	public void deleteLineByCartAndProduct(Cart cart, Product product) {
		delete(new CartDetailId(cart.getCartId(), product.getUpc()));
	}
	public void delete(CartDetail item) {
		cartDetailRepository.delete(item);
	}
}