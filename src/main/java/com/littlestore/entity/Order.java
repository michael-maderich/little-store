package com.littlestore.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Basic;
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
@Table(name="orders")
public class Order implements Serializable {

	private static final long serialVersionUID = 1234567890L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="orderNum", nullable=false, unique=true)
	private int orderNum;

	@ManyToOne(targetEntity=Customer.class, optional=false)
	@JoinColumn(name = "customerId", nullable=false)
	private Customer customer;
	
	@Basic
	@Column(name="orderDateTime", nullable=false)
	private LocalDateTime orderDateTime;
	
	@Basic
	@Column(name="reqDeliveryDateTime")
	private LocalDateTime reqDeliveryDateTime;
	
	@Basic
	@Column(name="status", length=20)
	private String status;				// 'Unshipped','Paid','Delivered'
	
	@Basic
	@Column(name="comments", length=250)
	private String comments;

	@OneToMany(targetEntity=OrderDetail.class, mappedBy="order")
	private List<OrderDetail> orderItems;

	public Order() {
	}

	public Order(int orderNum, Customer customer, LocalDateTime orderDateTime, LocalDateTime reqDeliveryDateTime,
			String status, String comments, List<OrderDetail> orderItems) {
		this.orderNum = orderNum;
		this.customer = customer;
		this.orderDateTime = LocalDateTime.now();	// orderDateTime;
		this.reqDeliveryDateTime = reqDeliveryDateTime;
		this.status = status;
		this.comments = comments;
		this.orderItems = orderItems;
	}

	
	public int getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LocalDateTime getOrderDateTime() {
		return orderDateTime;
	}
	public void setOrderDateTime(LocalDateTime orderDateTime) {
		this.orderDateTime = orderDateTime;
	}

	public LocalDateTime getReqDeliveryDateTime() {
		return reqDeliveryDateTime;
	}
	public void setReqDeliveryDateTime(LocalDateTime reqDeliveryDateTime) {
		this.reqDeliveryDateTime = reqDeliveryDateTime;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<OrderDetail> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderDetail> orderItems) {
		this.orderItems = orderItems;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((orderDateTime == null) ? 0 : orderDateTime.hashCode());
		result = prime * result + ((orderItems == null) ? 0 : orderItems.hashCode());
		result = prime * result + orderNum;
		result = prime * result + ((reqDeliveryDateTime == null) ? 0 : reqDeliveryDateTime.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Order))
			return false;
		Order other = (Order) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (orderDateTime == null) {
			if (other.orderDateTime != null)
				return false;
		} else if (!orderDateTime.equals(other.orderDateTime))
			return false;
		if (orderItems == null) {
			if (other.orderItems != null)
				return false;
		} else if (!orderItems.equals(other.orderItems))
			return false;
		if (orderNum != other.orderNum)
			return false;
		if (reqDeliveryDateTime == null) {
			if (other.reqDeliveryDateTime != null)
				return false;
		} else if (!reqDeliveryDateTime.equals(other.reqDeliveryDateTime))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
}
