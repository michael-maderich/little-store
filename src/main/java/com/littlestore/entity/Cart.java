package com.littlestore.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Basic;
//import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="cart")
public class Cart implements Serializable {

	private static final long serialVersionUID = 1234567890L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cartId", nullable=false, unique=true)
	private int cartId;
	
	@ManyToOne(targetEntity=Customer.class, optional=false)
	@JoinColumn(name = "customerId", nullable=false)
	private Customer customer;
	
	@Basic
	@Column(name="cartCreationDateTime", nullable=false)
	private LocalDateTime cartCreationDateTime;
	
	@OneToMany(targetEntity=CartDetail.class, mappedBy="cart")//, cascade=CascadeType.ALL)
	private List<CartDetail> cartItems;

	public Cart() {
	}

	public Cart(int cartId, Customer customer, LocalDateTime cartCreationDateTime, List<CartDetail> cartItems) {
		this.cartId = cartId;
		this.customer = customer;
		this.cartCreationDateTime = cartCreationDateTime;
		this.cartItems = cartItems;
	}

	public int getCartId() {
		return cartId;
	}
	public void setCartId(int cartId) {
		this.cartId = cartId;
	}

	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LocalDateTime getCartCreationDateTime() {
		return cartCreationDateTime;
	}
	public void setCartCreationDateTime(LocalDateTime cartCreationDateTime) {
		this.cartCreationDateTime = cartCreationDateTime;
	}

	public List<CartDetail> getCartItems() {
		return cartItems;
	}
	public void setCartItems(List<CartDetail> cartItems) {
		this.cartItems = cartItems;
	}

	
	@Override
	public String toString() {
		return "Cart [\tcartId=" + cartId + ",\n\t" + customer + ",\n\tcartCreationDateTime = " + cartCreationDateTime
				+ ",\n\tcartItems:" + cartItems + "\n     ]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cartCreationDateTime == null) ? 0 : cartCreationDateTime.hashCode());
		result = prime * result + cartId;
		result = prime * result + ((cartItems == null) ? 0 : cartItems.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Cart))
			return false;
		Cart other = (Cart) obj;
		if (cartCreationDateTime == null) {
			if (other.cartCreationDateTime != null)
				return false;
		} else if (!cartCreationDateTime.equals(other.cartCreationDateTime))
			return false;
		if (cartId != other.cartId)
			return false;
		if (cartItems == null) {
			if (other.cartItems != null)
				return false;
		} else if (!cartItems.equals(other.cartItems))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		return true;
	}
}
