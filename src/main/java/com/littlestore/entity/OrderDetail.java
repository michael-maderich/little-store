package com.littlestore.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@IdClass(OrderDetailId.class)
@Table(name="orderDetail")
public class OrderDetail implements Serializable, Comparable<OrderDetail> {

	private static final long serialVersionUID = 123453367890L;

	@Id
	@ManyToOne(targetEntity=Order.class, fetch=FetchType.LAZY)
	@JoinColumn(name="orderNum", nullable=false)
	private Order order;
	
	@Id
	@ManyToOne(targetEntity=Product.class, fetch=FetchType.LAZY)
	@JoinColumn(name="upc", nullable=false)
	private Product product;
	
	@Basic
	@Column(name="description", nullable=false)
	private String description;
	
	@Basic
	@Column(name="image", nullable=false)
	private String image;
	
	@Basic
	@Column(name="qty", nullable=false)
	private int qty;
	
	@Basic
	@Column(name="price", nullable=false)
	private float price;
	
	@Basic
	@Column(name="lineNumber", nullable=false)
	private int lineNumber;

	public OrderDetail() {
	}

	public OrderDetail(Order order, Product product, String description, String image, int qty, float price, int lineNumber) {
		this.order = order;
		this.product = product;
		this.description = description;
		this.image = image;
		this.qty = qty;
		this.price = price;
		this.lineNumber = lineNumber;
	}

	
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
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
	public int compareTo(OrderDetail o) {		// Order by Main Category, Specific Category, name, size, options
		int compare = getProduct().getCategoryMain().compareTo(o.getProduct().getCategoryMain());
		if (compare == 0) compare = getProduct().getCategorySpecific().compareTo(o.getProduct().getCategorySpecific());
		if (compare == 0) compare = getProduct().getName().compareTo(o.getProduct().getName());
		if (compare == 0) compare = getProduct().getSize().compareTo(o.getProduct().getSize());
		if (compare == 0) compare = getProduct().getOptions().compareTo(o.getProduct().getOptions());
		return compare;
	}
	

}
