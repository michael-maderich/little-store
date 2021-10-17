package com.littlestore.entity;

import java.io.Serializable;

public class OrderDetailId implements Serializable {

	private static final long serialVersionUID = 1234567892L;

	private int order;

    private String product;

    
    public OrderDetailId() {
	}

	public OrderDetailId(int order, String product) {
        this.order = order;
        this.product = product;
    }

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + order;
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderDetailId other = (OrderDetailId) obj;
		if (order != other.order)
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		return true;
	}
}