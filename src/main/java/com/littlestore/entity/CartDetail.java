package com.littlestore.entity;

import java.io.Serializable;

import javax.persistence.Basic;
//import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@IdClass(CartDetailId.class)
@Table(name="cartDetail")
public class CartDetail implements Serializable, Comparable<CartDetail> {

	private static final long serialVersionUID = 9875647890L;

	@Id
	@ManyToOne(targetEntity=Cart.class, fetch=FetchType.LAZY)//, cascade=CascadeType.MERGE)
	@JoinColumn(name="cartId", nullable=false)
	private Cart cart;
	
	@Id
	@ManyToOne(targetEntity=Product.class, fetch=FetchType.LAZY)
	@JoinColumn(name="upc", nullable=false)
	private Product product;
	
	@Basic
	@Column(name="qty", nullable=false)
	private int qty;
	
	@Basic
	@Column(name="basePrice", nullable=false)
	private float basePrice;
	
	@Basic
	@Column(name="price", nullable=false)
	private float price;
	
	@Basic
	@Column(name="lineNumber", nullable=false)
	private int lineNumber;

	public CartDetail() {
	}

	public CartDetail(Cart cart, Product product, int qty, float basePrice, float price, int lineNumber) {
		this.cart = cart;
		this.product = product;
		this.qty = qty;
		this.basePrice = basePrice;
		this.price = price;
		this.lineNumber = lineNumber;
	}

	
	public Cart getCart() {
		return cart;
	}
	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}

	public float getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(float basePrice) {
		this.basePrice = basePrice;
	}

	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return "\n\t\tCartDetail [" + product + ", qty=" + qty + ", basePrice=" + basePrice + ", price=" + price + ", lineNumber=" + lineNumber + "]";
	}

	@Override			// Could probably just implement a custom toString() for CartDetail and compare that
	public int compareTo(CartDetail o) {		// Order by Main Category, Specific Category, name, size, options
		int compare = getProduct().getCategoryMain().compareTo(o.getProduct().getCategoryMain());
		if (compare == 0) compare = getProduct().getCategorySpecific().compareTo(o.getProduct().getCategorySpecific());
		if (compare == 0) compare = getProduct().getName().compareTo(o.getProduct().getName());
		if (compare == 0) compare = getProduct().getSize().compareTo(o.getProduct().getSize());
		if (compare == 0) compare = getProduct().getOptions().compareTo(o.getProduct().getOptions());
		return compare;
	}
}
