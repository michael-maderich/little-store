package com.littlestore.entity;

import java.io.Serializable;

public class CartDetailId implements Serializable {

	private static final long serialVersionUID = 123456789L;

	private int cart;

    private String product;

    
    public CartDetailId() {
	}

	public CartDetailId(int cart, String product) {
        this.cart = cart;
        this.product = product;
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cart;
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
		CartDetailId other = (CartDetailId) obj;
		if (cart != other.cart)
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		return true;
	}
}