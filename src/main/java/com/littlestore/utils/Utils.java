package com.littlestore.utils;

//import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.littlestore.entity.Cart;

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
}