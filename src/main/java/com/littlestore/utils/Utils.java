package com.littlestore.utils;

import java.lang.reflect.Field;
//import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;

import com.littlestore.entity.Cart;
import com.littlestore.entity.Product;

public class Utils {

	public static int currentYear() {
//		Date date = new Date();
//		ZoneId timeZone = ZoneId.systemDefault();
//		LocalDate getLocalDate = date.toInstant().atZone(timeZone).toLocalDate();
//		return getLocalDate.getYear();
		return new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
	}
	
	// Products in the cart, stored in Session.
	public static Cart getCartInSession(HttpServletRequest request) {

		Cart cart = (Cart) request.getSession().getAttribute("customerCart");

		if (cart == null) {
			cart = new Cart();

			request.getSession().setAttribute("customerCart", cart);
		}

		return cart;
	}

	public static void removeCartInSession(HttpServletRequest request) {
		request.getSession().removeAttribute("customerCart");
	}

	public static void storeLastOrderedCartInSession(HttpServletRequest request, Cart cart) {
		request.getSession().setAttribute("lastOrderedCart", cart);
	}

	public static Cart getLastOrderedCartInSession(HttpServletRequest request) {
		return (Cart) request.getSession().getAttribute("lastOrderedCart");
	}

	/** 
	 * Returns the JPA @Column.length for the given field on the given entity class,
	 * or fallbackValue if the annotation is missing.
	 */
	public static Integer getColumnLength(Class<?> entityClass, String fieldName, int fallbackValue) {
	  try {
	    Field f = entityClass.getDeclaredField(fieldName);
	    Column col = f.getAnnotation(Column.class);
	    return (col != null && col.length() > 0)
	      ? col.length()
	      : fallbackValue;
	  } catch (NoSuchFieldException e) {
	    return fallbackValue;
	  }
	}
}