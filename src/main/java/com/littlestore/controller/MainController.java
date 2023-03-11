package com.littlestore.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.littlestore.entity.Cart;
import com.littlestore.entity.CartDetail;
import com.littlestore.entity.Customer;
import com.littlestore.entity.Order;
import com.littlestore.entity.OrderDetail;
//import com.littlestore.entity.OrderDetail;
import com.littlestore.entity.Product;
import com.littlestore.service.CartDetailService;
//import com.littlestore.pagination.PaginationResult;
import com.littlestore.service.CartService;
import com.littlestore.service.CustomerService;
import com.littlestore.service.OrderDetailService;
import com.littlestore.service.OrderService;
import com.littlestore.service.ProductService;
import com.littlestore.service.SecurityService;
import com.littlestore.validator.CustomerFormValidator;

import com.littlestore.utils.SendSimpleEmail;

/**
 * @author Michael Maderich
 *
 */
@Controller
public class MainController {
	
	@Autowired private CustomerService customerService;
	@Autowired private ProductService productService;
	@Autowired private CartService cartService;
	@Autowired private CartDetailService cartDetailService;
	@Autowired private OrderService orderService;
	@Autowired private OrderDetailService orderDetailService;
	@Autowired private SecurityService securityService;
	@Autowired private CustomerFormValidator customerFormValidator;
	
	private List<String> listStates = Stream.of(Customer.States.values()).map(Enum::name).collect(Collectors.toList());

	private List<String> listPayTypes = Stream.of(Customer.PaymentMethods.values()).map(Enum::name).collect(Collectors.toList());
	
	private	List<String> getNavMenuItems() {
//		return productService.listCategoryMain();
		return productService.listCategoryMainWithStock();
	}

	private List<String> getNavSubMenuItems(String categoryName) {
//		return productService.listCategorySpecificUnderMain(categoryName);
		return productService.listCategorySpecificUnderMainWithStock(categoryName);
	}
	
	private Customer getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			return customerService.findByEmail(currentUserName);
		}
		else return null;
	}
	
	private String updateCartChanges() {
		String error = "";
		Customer customer = getLoggedInUser();
		Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
		if (customerCart == null) {								// If not cart, no changes
			return null;
		}
		List<CartDetail> cartItems = new ArrayList<>(customerCart.getCartItems());
		int i = 0;
		while (i < cartItems.size()) {
			Product checkedProduct = productService.get(cartItems.get(i).getProduct().getUpc());
			int diff = cartItems.get(i).getQty() - checkedProduct.getStockQty();
			if (diff > 0) {
				if (checkedProduct.getStockQty() == 0) {
					CartDetail removedLineItem = cartDetailService.findLineByCartAndProduct(customerCart, cartItems.get(i).getProduct());
					cartItems.remove(removedLineItem);
					cartDetailService.delete(removedLineItem);
//					Collections.sort(cartItems);			// CartDetail entity contains compareTo() method
					i--;		// Counteract i++ since list size has decreased
				}
				else {
					cartItems.get(i).setQty(checkedProduct.getStockQty());	// Set cart qty to available qty
					cartDetailService.save(cartItems.get(i));	// Will overwrite any previous cartDetail with same composite key (cartId/upc)
				}
				float cartPrice = cartItems.get(i).getPrice();
				if (cartPrice != checkedProduct.getCurrentPrice()) {
					error += (error.isEmpty() ? "Notice: " : "") + "The price of " + checkedProduct.getDescription() + " has changed from $"
							+ String.format("%.2f", cartItems.get(i).getPrice()) + " to $" + String.format("%.2f", checkedProduct.getCurrentPrice())
							+ "  since it was added to your cart.<br/>";
					cartItems.get(i).setPrice(checkedProduct.getCurrentPrice());
					cartDetailService.save(cartItems.get(i));	// Will overwrite any previous cartDetail with same composite key (cartId/upc)
				}
				error += (error.isEmpty() ? "Notice: " : "") + "The available qty of " + checkedProduct.getDescription() + " has changed. "
						+ diff + (diff == 1 ? " was":" were") + " removed from your cart.<br/>";
			}
			i++;
		}
		customerCart.setCartItems(cartItems);
		if (cartItems.isEmpty()) cartService.delete(customerCart);	// If no cart items left available, we want creation time to reset
		else cartService.save(customerCart);
/**/		System.out.println(customerCart);
		return error;
	}
	
	@GetMapping("/403")
	public String accessDenied() {
		  return "/403";
	}
	

	@GetMapping("/{nonsense}")
	public String badUrl(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		return "redirect:/";
	}
	
	// Mapping to root/home/index page
	@GetMapping({"/", "home", "/index"})
	public String home(Model model) {
		String cartAdjustments = null;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Customer customer = getLoggedInUser();
		if (customer != null) {										// If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			}
		}
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		return "/index";
	}

	@GetMapping("/login")
	public String login(Model model, String error, String logout) {
		if (error != null) model.addAttribute("error", "Your username and/or password is invalid.");
		if (logout != null) model.addAttribute("message", "You have been logged out successfully.");
		model.addAttribute("navMenuItems", getNavMenuItems());
		if (getLoggedInUser() != null) return "/newitems";				// If user is logged in, redirect to newitems page
		else return "/login";											// Otherwise, submit POST request to login page (handled by Spring Security)
	}

	@GetMapping("/signup")
	public String registration(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());

		if (getLoggedInUser() != null) return "redirect:/account";		// If user is already signed in, redirect to account page.
		else {
			model.addAttribute("customerForm", new Customer());
			model.addAttribute("listStates", listStates);
			return "/signup";
		}
	}
	
	// When registration form is submitted, the signup page sends a POST request to itself.
	// The user info submitted is validated and return back to signup page if there are errors.
	// If the info submitted is valid, persist the customer data to the database
	@PostMapping("/signup")
	public String registration(Model model, @ModelAttribute("customerForm") Customer customerForm, BindingResult bindingResult) {
		model.addAttribute("navMenuItems", getNavMenuItems());

		customerFormValidator.validate(customerForm, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("listStates", listStates);	// States enum value list needs to be sent to signup page every time. I'm sure there's a better way to do this
			return "/signup";
		}
		else {
			customerForm.setIsEnabled(true);
			customerForm.setAccountCreated(LocalDateTime.now().minusHours(5));
			customerService.create(customerForm);
			securityService.autoLogin(customerForm.getEmail(), customerForm.getPasswordConfirm());
			model.addAttribute("listStates", listStates);
			return "/account";
		}
	}

	// Page for password reset request
	@GetMapping("/forgotPassword")
	public String forgotPassword(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		return "/forgotPassword";
	}
	
	// Page to change user password
	@GetMapping("/passwordReset")
	public String passwordReset(Model model, @RequestParam(value = "code", defaultValue="") String code) {
		model.addAttribute("navMenuItems", getNavMenuItems());

		if (getLoggedInUser() != null) return "redirect:/account";		// If user is already signed in, redirect to account page.
		else {
//			Customer customer = customerService.findByEmail(code);
			for (Customer customer : customerService.listAll())
			{
				if (customerService.emailCodeMatches(customer, code))
//				if (customer != null)
				{
					customer.setPassword(null);
					model.addAttribute("customerForm", customer);
					model.addAttribute("listStates", listStates);
					return "/passwordReset";
				}
			}
			return "redirect:/login";
		}
	}
	
	// When password reset form is submitted, the page sends a POST request to itself.
	// The user info submitted is validated and return back to signup page if there are errors.
	// If the info submitted is valid, persist the customer data to the database
	@PostMapping("/passwordReset")
	public String resetPassword(Model model, @ModelAttribute("customerForm") Customer customerForm, BindingResult bindingResult) {
		model.addAttribute("navMenuItems", getNavMenuItems());

		customerFormValidator.validatePasswordReset(customerForm, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("listStates", listStates);	// States enum value list needs to be sent to signup page every time. I'm sure there's a better way to do this
			return "/passwordReset";
		}
		else {
			Customer customer = customerService.findByEmail(customerForm.getEmail());
			customer.setPassword(customerService.encrypt(customerForm.getPassword()));
			customerService.update(customer);
			securityService.autoLogin(customerForm.getEmail(), customerForm.getPasswordConfirm());
			model.addAttribute("listStates", listStates);
			model.addAttribute("message", "Your password has been successfully changed. Please log in with your new password.");
			return "/login";
		}
	}

	@GetMapping("/encode/{email}")
	public String encodeText(Model model, @PathVariable(name="email") String email) {
		String encodedText = customerService.encrypt(email);
		model.addAttribute("encodedText", encodedText);
		return "/encode";
	}

	// Page for user to view/edit Profile, view Order History, and other functions TBD
	@GetMapping("/account")
	public String accountPage(Model model) {
//		model.addAttribute("navMenuItems", getNavMenuItems());

		Customer customer = getLoggedInUser();
		if (customer == null) {
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("error", "You must be logged in to view your account.");
			return "/login";
		}
		else {
			int cartTotalItemQty = 0;
			float cartTotalItemCost = 0.0f;
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			}
			model.addAttribute("customerForm", customer);
			model.addAttribute("listStates", listStates);
			return "/account";
		}
	}
	
	// For editing a customer? I forget. Yes, I think so.
	@PostMapping("/account")
	public String accountChange(Customer customer) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
//			String currentUserName = authentication.getName();
			customerService.update(customer);
//			model.addAttribute("listStates", listStates);
			return "/account";
		}
		return "redirect:/login";
	}

	// View account's order history - user must be logged in
	@GetMapping("/account/edit")
	public String editAccount(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		Customer customer = getLoggedInUser();
		if (customer == null) {				// Can't view orders if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("error", "You must be logged in to edit your account details.");
			return "/login";
		}
		else {
			int cartTotalItemQty = 0;
			float cartTotalItemCost = 0.0f;
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			}
			model.addAttribute("customerForm", customer);
			model.addAttribute("listStates", listStates);
			return "/account";
		}
	}

	// View account's order history - user must be logged in
	@GetMapping("/account/orders")
	public String orderHistory(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		Customer customer = getLoggedInUser();
		if (customer == null) {				// Can't view orders if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("error", "You must be logged in to view your orders.");
			return "/login";
		}
		else {
			int cartTotalItemQty = 0;
			float cartTotalItemCost = 0.0f;
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			}
			model.addAttribute("customer", customer);
			List<Order> orderList = orderService.findByCustomer(customer);
			Collections.reverse(orderList);	// Show most recent order first
			model.addAttribute("orderList", orderList);
			return "/order";
		}
		
	}

	// Only need String likeName for Search, empty default returns all Products
/*	@GetMapping({ "/productList" })
		public String listProductHandler(Model model,			// Pagination - haha yeah, one day
					@RequestParam(value = "name", defaultValue = "") String likeName,
					@RequestParam(value = "page", defaultValue = "1") int page) {
			final int maxResult = 5;
			final int maxNavigationPage = 10;
		 
			PaginationResult<Product> result = productService.queryProducts(page,
		maxResult, maxNavigationPage, likeName);
		 
		model.addAttribute("paginationProducts", result);
		return "productList";
	}*/
	
	@GetMapping("/category/")
	public String categoryRootRedirect() {
		return "redirect:/category/Laundry";
	}
	
	@GetMapping("/category/{categoryName}")
	public String listItemsInCategory(Model model, @PathVariable(name="categoryName") String categoryName,
										@RequestParam(value = "addedUpc", defaultValue="") String addedUpc,
										@RequestParam(value = "addedItemQty", defaultValue="0") String addedItemQty) {
		List<Product> itemList = !(categoryName.equalsIgnoreCase("Christmas Shop"))// || categoryName.equalsIgnoreCase("Laundry"))
													?  productService.findByCategoryMainMinQtySorted(categoryName, 0)
/*		List<Product> itemList = */					: productService.findByCategoryMainSorted(categoryName);
		String cartAdjustments = null;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		boolean goodLink = false;
		for (Product p : itemList) if ( p.getCategoryMain().equals(categoryName) ) goodLink = true;
		if (!goodLink) return "redirect:/";

		Customer customer = getLoggedInUser();
		if (customer != null) {										// If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("cartItems", cartItems);
			}
		}
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("navSubMenuItems", getNavSubMenuItems(categoryName));
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		return "category";
	}

	@GetMapping("/category/{categoryName}/{subCategoryName}")
	public String listItemsInSubCategory(@PathVariable(name="categoryName") String categoryName,
										@PathVariable(name="subCategoryName") String subCategoryName, Model model,
										@RequestParam(value = "addedUpc", defaultValue="") String addedUpc,
										@RequestParam(value = "addedItemQty", defaultValue="0") String addedItemQty) {
		List<Product> itemList = !(categoryName.equalsIgnoreCase("Christmas Shop"))// || categoryName.equalsIgnoreCase("Laundry"))
									? productService.findByCategorySpecificMinQtySorted(subCategoryName, 0)	// Filter out 0 qty items
/*		List<Product> itemList = */	: productService.findByCategorySpecificSorted(subCategoryName);			// Shows 0 qty items
		String cartAdjustments = null;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		boolean goodLink = false;
		for (Product p : itemList) if ( p.getCategoryMain().equals(categoryName) 
									&& p.getCategorySpecific().equals(subCategoryName) ) goodLink = true;
		if (!goodLink) return "redirect:/";
		
		Customer customer = getLoggedInUser();
		if (customer != null) {										// If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("cartItems", cartItems);
			}
		}

		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("navSubMenuItems", getNavSubMenuItems(categoryName));
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		return "category";
	}

	@GetMapping("/images")
	public String showCombinedImages(Model model) {
		List<ArrayList<ArrayList<ArrayList<Product>>>> itemList = productService.findAllByCatAndSubcat();	// List<Cat<Subcat items>>
		
		model.addAttribute("allItems", itemList);
		model.addAttribute("navMenuItems", getNavMenuItems());
		return "images";
	}
	
	@GetMapping("/newitems")
	public String showNewItems(Model model, @RequestParam(value = "addedUpc", defaultValue="") String addedUpc,
										@RequestParam(value = "addedItemQty", defaultValue="0") String addedItemQty) {
		String cartAdjustments = "";
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Customer customer = getLoggedInUser();
		if (customer == null) {				// Can't view new items if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("error", "Since items shown as new are based on your last order date, you must be logged in to view new items.");
			return "/login";
		}
		else {										// If a User is logged in, get their cart, (or null if it doesn't exist)
			List<Product> itemList = productService.getNewItems(customer.getId());
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("cartItems", cartItems);
			}
			model.addAttribute("cartAdjustments", cartAdjustments);
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("addedUpc", addedUpc);
			model.addAttribute("addedItemQty", addedItemQty);
			model.addAttribute("itemList", itemList);
		}
		return "newitems";
	}

	@GetMapping("/dollarama")
	public String showDollarItems(Model model, @RequestParam(value = "addedUpc", defaultValue="") String addedUpc,
										@RequestParam(value = "addedItemQty", defaultValue="0") String addedItemQty) {
		String cartAdjustments = "";
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Customer customer = getLoggedInUser();
		if (customer != null) {										// If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("cartItems", cartItems);
			}
		}
		List<Product> itemList = productService.getDollarItems();
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		return "dollarama";
	}

	@GetMapping("/sale")
	public String showSaleItems(Model model, @RequestParam(value = "addedUpc", defaultValue="") String addedUpc,
										@RequestParam(value = "addedItemQty", defaultValue="0") String addedItemQty) {
		String cartAdjustments = "";
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Customer customer = getLoggedInUser();
		if (customer != null) {										// If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("cartItems", cartItems);
			}
		}
		List<Product> itemList = productService.getSaleItems();
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		return "sale";
	}

	@GetMapping("/search")
	public String itemSearch(Model model, @RequestParam(value = "q", defaultValue="") String searchText,
							@RequestParam(value = "addedUpc", defaultValue="") String addedUpc,
							@RequestParam(value = "addedItemQty", defaultValue="0") String addedItemQty) {
		List<Product> itemList = productService.getSearchResults(searchText);
		String cartAdjustments = null;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;

		Customer customer = getLoggedInUser();
		if (customer != null) {										// If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null)
			{		// If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("cartItems", cartItems);
			}
		}
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		model.addAttribute("searchText", searchText);
		return "searchresults";
	}

	@GetMapping("/addToCart")
	public String addItemsToCart(HttpServletRequest request, Model model,
								@RequestParam(value = "q", defaultValue="") String searchText,
								@RequestParam(value = "upc", defaultValue="") String upc,
								@RequestParam(value = "itemQty", defaultValue="0") String itemQty) {

		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		
		String referer = request.getHeader("Referer");						// http://localhost:8080/xxxxxx - we just want the "xxxxxx"
		if (referer==null) return "redirect:/index";					// If page request didn't come from product page, reject and return to cart
		else {
			referer = referer.substring( referer.indexOf('/', referer.indexOf('/')+2) );		// everything after root '/', including the /
			referer = referer.substring(0, (referer.indexOf('?') != -1) ? referer.indexOf('?') : referer.length());	// remove the query string if exists
			if (!( referer.startsWith("/category") || referer.startsWith("/newitems")
				|| referer.startsWith("/dollarama") || referer.startsWith("/search") || referer.startsWith("/sale") ))
				return "redirect:"+referer;
		}

		Customer customer = getLoggedInUser();
		Cart customerCart;
		Product purchasedProduct;
		int purchasedQty = Integer.parseInt(itemQty);		// Can't throw exception because referrer string format already checked
		int addedItemQty = purchasedQty;
		try {												// Irrelevant since referrer string checked, but maybe missed something
			purchasedProduct = productService.get(upc);
		}
		catch (NoSuchElementException e) {
			return "redirect:" + referer;
		}

		if (customer == null) {				// Can't add to cart if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("error", "You must be logged in to add items to your cart.");
			return "/login";
		}
		else {													// If a User is logged in, get their cart, (or null if it doesn't exist)
			if (addedItemQty == 0) return "redirect:"+referer;	// 0 is a valid qty option, but we don't want to add that to the cart
			customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null) {							// If they don't have a cart started, start a new one
				customerCart = new Cart();
				customerCart.setCustomer(customer);
				customerCart.setCartCreationDateTime(LocalDateTime.now().minusHours(5));
				customerCart.setCartItems(new ArrayList<CartDetail>());
				cartService.save(customerCart);					// New cart needs to be saved before items can be added because of Foreign Key relationship
			}
			List<CartDetail> cartItems = new ArrayList<>(customerCart.getCartItems());
			int lineNum = 0;
			for (CartDetail item : cartItems) {
				if (item.getProduct().getUpc() == upc) {		// One or more of this item is already in the cart, so just increase qty
					purchasedQty += item.getQty();				// Add qty already in cart to amount added to cart
					if (purchasedQty > item.getProduct().getStockQty())	{	// If more than available stock is requested..
						purchasedQty = item.getProduct().getStockQty();		// Lower purchased qty to available stock
						addedItemQty = item.getProduct().getStockQty() - item.getQty();	// How many were actually added
					}
					else if (purchasedQty > item.getProduct().getPurchaseLimit()) {	// If more than purchase limit is requested..
						purchasedQty = item.getProduct().getPurchaseLimit();		// Lower purchased qty to purchase limit
						addedItemQty = item.getProduct().getPurchaseLimit() - item.getQty();	// How many were actually added
					}
//					item.setQty(item.getQty()+purchasedQty);	// Don't set now..
					cartItems.remove(item);						// delete and recreate instead for smoother code
					lineNum = item.getLineNumber() - 1;			// Get the item's line number -1 because will ++ after loop
					break;	// out of foreach loop
				}
				else if (item.getLineNumber() > lineNum) lineNum = item.getLineNumber();	// Get new line number based on max existing
			}
			lineNum++;

			CartDetail newLineItem = new CartDetail(customerCart, purchasedProduct, purchasedQty, purchasedProduct.getCurrentPrice(), lineNum);
			cartItems.add(newLineItem);
			Collections.sort(cartItems);			// CartDetail entity contains compareTo() method. List sorted for better cart/checkout display
			customerCart.setCartItems(cartItems);
			cartDetailService.save(newLineItem);	// Will overwrite any previous cartDetail with same composite key (cartId/upc)
			cartService.save(customerCart);
//			purchasedProduct.setStockQty(purchasedProduct.getStockQty()-addedItemQty);	// Remove items in carts from available qty
			/* System.out.println(customerCart); */

			for (CartDetail detail : cartItems) {
				cartTotalItemQty += detail.getQty();
				cartTotalItemCost += detail.getQty() * detail.getPrice();
			}
			model.addAttribute("cartTotalItemQty", cartTotalItemQty);
			model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			model.addAttribute("customerCart", customerCart);
			model.addAttribute("searchText", searchText);
			return !referer.startsWith("/search") ? "redirect:" + referer+"?addedUpc="+upc+"&addedItemQty="+addedItemQty
												  : "redirect:" + referer + "?q=" + searchText +"&addedUpc="+upc+"&addedItemQty="+addedItemQty;
		}
	}

	@GetMapping("/cart")
	public String cart(Model model) {
		String cartAdjustments;
		Cart customerCart;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Customer customer = getLoggedInUser();
		if (customer == null) {				// Can't view cart if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("error", "You must be logged in to view your cart.");
			model.addAttribute("navMenuItems", getNavMenuItems());
			return "redirect:/login";
		}
		else {										// If a User is logged in, get their cart, (or null if it doesn't exist)
			customerCart = cartService.findByCustomerEmail(customer.getEmail());		// Possibly null if no cart started, but handles fine
			if (customerCart == null) {			// If they don't have a cart, redirect to cart page but couldn't get here unless url typed/bookmarked
				model.addAttribute("customer", customer);
				model.addAttribute("customerCart", null);
				return "/cart";
			}
			cartAdjustments = updateCartChanges();
			List<CartDetail> cartItems = customerCart.getCartItems();
			Collections.sort(cartItems);			// CartDetail entity contains compareTo() method
			customerCart.setCartItems(cartItems);
			for (CartDetail detail : cartItems) {
				cartTotalItemQty += detail.getQty();
				cartTotalItemCost += detail.getQty() * detail.getPrice();
			}
			model.addAttribute("cartTotalItemQty", cartTotalItemQty);
			model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			model.addAttribute("customer", customer);
			model.addAttribute("customerCart", customerCart);
			model.addAttribute("cartAdjustments", cartAdjustments);
			return "/cart";
		}
	}
	
	@GetMapping("/removeFromCart")
	public String removeItemsFromCart(Model model,
								@RequestParam(value = "upc", defaultValue="") String upc) {

		Customer customer = getLoggedInUser();
		Cart customerCart;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Product removedProduct;
		try {	// This block only necessary if bad query string, which would only happen if url entered manually
			removedProduct = productService.get(upc);
		}
		catch (NoSuchElementException e) {
			return "redirect:/cart";
		}
		if (customer == null) {		// Can't edit cart if not logged in, but also can't get here since can't access cart, either, unless url typed
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("error", "You must be logged in to edit your cart.");
			return "/login";
		}
		else {														// If a User is logged in, get their cart, (or null if it doesn't exist)
			customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null) {								// Once again, only accessible through url through bad request
				return "redirect:/cart";
			}
			List<CartDetail> cartItems = new ArrayList<>(customerCart.getCartItems());
			CartDetail removedLineItem;
			try {
				removedLineItem = cartDetailService.findLineByCartAndProduct(customerCart, removedProduct);				
			}
			catch (NoSuchElementException e) {			// An item that does not exist in the cart has been attempted to be removed, again manual URL
				return "redirect:/cart";
			}
			cartItems.remove(removedLineItem);
			Collections.sort(cartItems);			// CartDetail entity contains compareTo() method
			customerCart.setCartItems(cartItems);
			cartDetailService.deleteLineByCartAndProduct(customerCart, removedProduct);
			if (cartItems.isEmpty()) cartService.delete(customerCart);	// If customer empties cart and comes back later, we want creation time to reset
			else cartService.save(customerCart);
//			removedProduct.setStockQty(removedProduct.getStockQty() + removedLineItem.getQty());	// Return deleted items back to available stock
/**/		System.out.println(customerCart);

			for (CartDetail detail : cartItems) {
				cartTotalItemQty += detail.getQty();
				cartTotalItemCost += detail.getQty() * detail.getPrice();
			}
			model.addAttribute("cartTotalItemQty", cartTotalItemQty);
			model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			model.addAttribute("customer", customer);
			model.addAttribute("customerCart", customerCart);	
			return "/cart";
		}
	}

	@GetMapping("/clearCart")
	public String removeAllItemsFromCart(HttpServletRequest request, Model model) {

		String referer = request.getHeader("Referer");				// http://localhost:8080/xxxxxx - we just want the "xxxxxx"
		if (referer==null) return "redirect:/cart";					// If page request didn't come from the cart, reject it and return to cart
		else {
			referer = referer.substring( referer.indexOf('/', referer.indexOf("//")+2) );		// everything after root '/', inlcuding the /
			referer = referer.substring(0, (referer.indexOf('?') != -1) ? referer.indexOf('?') : referer.length());	// remove the query string if exists
			if (!referer.equals("/cart")) return "redirect:/cart";
		}

		Customer customer = getLoggedInUser();
		Cart customerCart;
		if (customer == null) {		// Can't delete cart if not logged in, but also can't get here since can't access cart, either, unless url typed
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("error", "You must be logged in to edit your cart.");
			return "/login";
		}
		else {														// If a User is logged in, get their cart, (or null if it doesn't exist)
			customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null) return "redirect:/cart";		// Once again, only accessible through url through bad request, but handle it
			List<CartDetail> cartItems = new ArrayList<>(customerCart.getCartItems());
			for (CartDetail item : cartItems) cartDetailService.delete(item);
			cartService.delete(customerCart);
			model.addAttribute("cartTotalItemQty", 0);
			model.addAttribute("customer", customer);
			return "redirect:/cart";
		}
	}

	@GetMapping("/checkout")
	public String orderFinalizationPage(Model model) {
		String cartAdjustments = "";
		Customer customer = getLoggedInUser();
		if (customer == null) {				// Can't check out if not logged in, for now. Direct user to log in
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("error", "Please log in to your account to check out.");
			return "/login";
		}
		else {														// If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null) {			// If they don't have a cart, redirect to cart page but couldn't get here unless url typed/bookmarked
				model.addAttribute("customer", customer);
				model.addAttribute("customerCart", customerCart);
				return "redirect:/cart";
			}
			// Reorganize cart so it's ordered by category/subcategory/name/options/size
			cartAdjustments = updateCartChanges();
			List<CartDetail> cartItems = customerCart.getCartItems();
			Collections.sort(cartItems);			// CartDetail entity contains compareTo() method
			model.addAttribute("customerInfo", customer);
			model.addAttribute("customerCart", customerCart);
			model.addAttribute("cartAdjustments", cartAdjustments);
			model.addAttribute("listStates", listStates);
			model.addAttribute("listPayTypes", listPayTypes);
			return "checkout";
		}
	}

	@PostMapping("/confirmation")
	public String completeOrder(Model model, @ModelAttribute("customerInfo") Customer customerUpdates) {
		
		Customer customer = getLoggedInUser();
		if (customer == null) {				// Can't complete order if not logged in, for now. Direct user to log in page
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("error", "Please log in to your account to check out.");
			return "/login";
		}
		else {														// If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null) {			// If they don't have a cart, redirect to cart page but couldn't get here unless url typed/bookmarked
				model.addAttribute("customer", customer);
				model.addAttribute("customerCart", customerCart);
				return "redirect:/cart";
			}
			// Add to Customer any updates to meeting address, phone/contact, payment method and payment handle
			customer.setPhone(customerUpdates.getPhone().trim().isEmpty() ? null : customerUpdates.getPhone().trim());
			customer.setAddress(customerUpdates.getAddress().trim().isEmpty() ? null : customerUpdates.getAddress().trim());
			customer.setCity(customerUpdates.getCity().trim().isEmpty() ? null : customerUpdates.getCity().trim());
			customer.setState(customerUpdates.getState());
			customer.setPreferredPayment(customerUpdates.getPreferredPayment());
			customer.setPaymentHandle(customerUpdates.getPaymentHandle().trim().isEmpty() ? null : customerUpdates.getPaymentHandle().trim());
			customer.setLastVisited(LocalDateTime.now().minusHours(5));
			customerService.update(customer);

			// Convert cart to Order and delete Cart
			Order customerOrder = new Order();
			customerOrder.setCustomer(customer);
			customerOrder.setOrderDateTime(LocalDateTime.now().minusHours(5));
			customerOrder.setReqDeliveryDateTime(null);
			customerOrder.setStatus("Confirmed");	// Will need to update this later using enum
			customerOrder.setComments(null);
			orderService.save(customerOrder);
			
			// Add each Cart Detail to Order Detail table
			List<CartDetail> cartItems = customerCart.getCartItems();
			Collections.sort(cartItems);
			List<OrderDetail> orderItems = new ArrayList<OrderDetail>();
			int lineNum = 1;
			for (CartDetail item : cartItems) {
				OrderDetail lineItem = new OrderDetail();
				lineItem.setOrder(customerOrder);
				lineItem.setProduct(item.getProduct());		//(product);
				lineItem.setQty(item.getQty());
				lineItem.setPrice(item.getPrice());
				lineItem.setLineNumber(lineNum++);
				orderItems.add(lineItem);
				orderDetailService.save(lineItem);			// Save Order line item
				cartDetailService.delete(item);				// Delete item from CartDetail table
				// Remove purchased qty from database
				String upc = item.getProduct().getUpc();
				Product product = productService.get(upc);
				product.setStockQty(product.getStockQty()-item.getQty());
				productService.save(product);
			}
			customerOrder.setOrderItems(orderItems);
			orderService.save(customerOrder);
			cartService.delete(customerCart);				// Remove the cart from DB

			// Craft order confirmation to be sent to customer's email address
			String emailBody =
			"<style>"+
			"	@charset \"ISO-8859-1\";"+
			"	#checkout-panel {padding-bottom:1em;}"+
			"	#checkout-panel h2, #checkout-panel h4 {font-family: 'Gravitas One', Verdana, Geneva, Tahoma, sans-serif;}"+
			"	#checkout-panel h4 {font-family: 'Inknut Antiqua', Verdana, Geneva, Tahoma, sans-serif;}"+
			"	#checkout-table {margin:0 auto; margin-top:2em;}"+
			"	.orderDetailHeader {margin-top:3em;}"+
			"	.orderDetailHeader span {padding-left:1em; padding-right:1em;}"+
			"	#checkout-table td, #checkout-table th {background-color:white; border:1px solid gray; padding-left:1em; padding-right:1em; padding-top:.5em; padding-bottom:.5em;}"+
			"	#checkout-table img {height:2em;}"+
			"	.checkout_image_panel {background-color:white;}"+
			"	.checkout_subtotal_panel {font-weight:bold;}"+
			"	.checkoutHeader {margin-top:2em; margin-bottom:1em;}"+
			"	.checkoutHeader span {padding-left:1em; padding-right:1em;}"+
			"	#customer-panel {padding-bottom:1em;}"+
			"	#customer-panel h2, #customer-panel h4 {font-family: 'Gravitas One', Verdana, Geneva, Tahoma, sans-serif;}"+
			"	#customer-panel h4 {font-family: 'Inknut Antiqua', Verdana, Geneva, Tahoma, sans-serif;}"+
			"	#customer-table {margin:0 auto; margin-top:2em;}"+
			"	#customer-table td {width:8em;}"+
			"	#customer-panel input, #customer-panel select {margin-bottom:.5em; background-color:white; color:black;}"+
			"	#customer-panel input::placeholder {color:black;}"+
			"	.customer_td_label {text-align:right; padding-bottom:.5em; padding-right:.5em;}"+
			"	.customer_td_input {text-align:left;}"+
			"	.text-field {width:16em;}"+
			"	#customer-panel p {margin-top:.75em; margin-bottom:.5em;}"+
			"	#customer-panel p span {font-size:.75em;}"+
			"	#city {width:13em;}"+
			"</style>"+
			"<div id='customer-panel'>\n"+
			"	<h2>Thank You For Your Order at <a href=\"https://the-little-store.herokuapp.com/\" target=\"_new\">The Little Store</a>!</h2>\n"+
			"	<h4 class='checkoutHeader'>Customer Details</h4>\n"+
			"	<table id='customer-table'>\n"+
			"		<tr>\n"+
			"			<td></td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='email'>Name:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_label'>\n"+
			"				"+customer.getFirstName()+" "+customer.getLastName()+"\n"+
			"			</td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='email'>Email:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+customer.getEmail()+"\n"+
			"			</td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='phone'>Phone:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+( customer.getPhone() == null || customer.getPhone().isEmpty() ? "(None Supplied)" : customer.getPhone() ) + "\n"+
			"			</td>\n"+
			"		</tr>\n"+
			"		<tr>\n"+
			"			<td colspan=2 class='customer_td_label'>\n"+
			"				<label for='address'>Meet-Up Address:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+( customer.getAddress()==null || customer.getAddress().isEmpty() ? "TBD" : customer.getAddress() ) +"\n"+
			"			</td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='city'>City:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+( customer.getCity()==null || customer.getCity().isEmpty() ? "TBD" : customer.getCity() ) +"\n"+
			"			</td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='state'>State:</label>\n"+
			"			</td>\n"+
			"			<td class='customer_td_input'>\n"+
			"				"+listStates.get(customer.getState().ordinal())+"\n"+
			"			</td>\n"+
			"		</tr>\n"+
			"		<tr>\n"+
			"			<td colspan=2 class='customer_td_label'>\n"+
			"				<label for='paymentType'>Payment Type:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+listPayTypes.get(customer.getPreferredPayment().ordinal())+"\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_label'>\n"+
			"				<label for='paymentHandle'>Payment Handle:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+customer.getPaymentHandle()+"\n"+
			"			</td>\n"+
			"		</tr>\n"+
			"	</table>\n"+
			"</div>\n"+
			"<div id='checkout-panel'>\n"+
			"	<h4 class='checkoutHeader'>Order Details</h4>\n"+
			"	<div class='orderDetailHeader'><h4>\n"+
			"		<span>Order #"+customerOrder.getOrderNum()+"</span>\n"+
			"		<span>Order Date: "+
			"			"+customerOrder.getOrderDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE)+"\n"+
			"		</span>\n"+
			"		<span>Status: "+customerOrder.getStatus()+"</span>\n"+
			"	</h4></div>\n"+
			"	<table id='checkout-table'>\n"+
			"		<thead>\n"+
			"			<tr>\n"+
			"				<th></th>\n"+
			"				<th>Item</th>\n"+
			"				<th>Scent/Style</th>\n"+
			"				<th>Size</th>\n"+
			"				<th>Quantity</th>\n"+
			"				<th>Unit Price</th>\n"+
			"				<th>Subtotal</th>\n"+
			"			</tr>\n"+
			"		</thead>\n"+
			"		<tbody>\n";
			double orderTotal = 0;
			for (OrderDetail orderItem : customerOrder.getOrderItems()) {
				emailBody +=
			"			<tr>\n"+
			"				<td class='checkout_image_panel' style='background-color:white;'>"+
			"					<img style='height:2em;' src='"+orderItem.getProduct().getImage()+"' alt='"+orderItem.getProduct().getDescription()+"' />"+
			"				</td>\n"+
			"				<td>"+orderItem.getProduct().getName()+"</td>\n"+
			"				<td>"+orderItem.getProduct().getOptions()+"</td>\n"+
			"				<td>"+orderItem.getProduct().getSize()+"</td>\n"+
			"				<td>"+orderItem.getQty()+"</td>\n"+
			"				<td>"+String.format("$%,.2f", orderItem.getPrice())+"</td>\n"+
			"				<td align=\"center\">"+String.format("$%,.2f", orderItem.getQty() * orderItem.getPrice())+"</td>\n"+
			"			</tr>\n";
				orderTotal += orderItem.getQty() * orderItem.getPrice();
			}
			emailBody +=
			"		</tbody>\n"+
			"		<tfoot>\n"+
			"			<tr>\n"+
			"				<td  colspan=6 style='text-align:right;' class='checkout_subtotal_panel'>Total:</td>\n"+
			"				<td class='checkout_subtotal_panel'>"+String.format("$%,.2f", orderTotal)+"</td>\n"+
			"			</tr>\n"+
			"		</tfoot>\n"+
			"	</table>\n"+
			"</div>";
			new SendSimpleEmail(customer.getEmail(), 
					 "Little Store Order #"+customerOrder.getOrderNum()+" Confirmation",
					 emailBody);
			// Send order notification to my email
			new SendSimpleEmail("thelittlestoregoods@gmail.com", "New Order Received", emailBody);
			// Send order notification to printer
			new SendSimpleEmail("jamitinmybox@hpeprint.com", "New Order Received", emailBody);

			
			model.addAttribute("customerInfo", customer);
			model.addAttribute("customerOrder", customerOrder);
			model.addAttribute("listStates", listStates);
			model.addAttribute("listPayTypes", listPayTypes);
			return "confirmation";
		}
	}

	@GetMapping("/confirmation/{email}/{orderNum}")
	public String resendOrderConfirmation(Model model, @PathVariable(name="email") String email, @PathVariable(name="orderNum") String orderNum) {
		Customer customer = customerService.findByEmail(email);
		if (customer == null) {				// Can't resend confirmation if user doesn't exist
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("error", "User email not found.");
			return "/login";
		}
		else {
			Order customerOrder = null;
			try {
				customerOrder = orderService.get(Integer.parseInt(orderNum));
			}
			catch (Exception e)
			{
				model.addAttribute("navMenuItems", getNavMenuItems());
				model.addAttribute("error", "Order number not found for user " + email + ".");
				return "/login";
			}
			
			// Add each Cart Detail to Order Detail table
			List<OrderDetail> orderItems = customerOrder.getOrderItems();
			Collections.sort(orderItems);

			// Craft order confirmation to be sent to customer's email address
			String emailBody =
			"<style>"+
			"	@charset \"ISO-8859-1\";"+
			"	#checkout-panel {padding-bottom:1em;}"+
			"	#checkout-panel h2, #checkout-panel h4 {font-family: 'Gravitas One', Verdana, Geneva, Tahoma, sans-serif;}"+
			"	#checkout-panel h4 {font-family: 'Inknut Antiqua', Verdana, Geneva, Tahoma, sans-serif;}"+
			"	#checkout-table {margin:0 auto; margin-top:2em;}"+
			"	.orderDetailHeader {margin-top:3em;}"+
			"	.orderDetailHeader span {padding-left:1em; padding-right:1em;}"+
			"	#checkout-table td, #checkout-table th {background-color:white; border:1px solid gray; padding-left:1em; padding-right:1em; padding-top:.5em; padding-bottom:.5em;}"+
			"	#checkout-table img {height:2em;}"+
			"	.checkout_image_panel {background-color:white;}"+
			"	.checkout_subtotal_panel {font-weight:bold;}"+
			"	.checkoutHeader {margin-top:2em; margin-bottom:1em;}"+
			"	.checkoutHeader span {padding-left:1em; padding-right:1em;}"+
			"	#customer-panel {padding-bottom:1em;}"+
			"	#customer-panel h2, #customer-panel h4 {font-family: 'Gravitas One', Verdana, Geneva, Tahoma, sans-serif;}"+
			"	#customer-panel h4 {font-family: 'Inknut Antiqua', Verdana, Geneva, Tahoma, sans-serif;}"+
			"	#customer-table {margin:0 auto; margin-top:2em;}"+
			"	#customer-table td {width:8em;}"+
			"	#customer-panel input, #customer-panel select {margin-bottom:.5em; background-color:white; color:black;}"+
			"	#customer-panel input::placeholder {color:black;}"+
			"	.customer_td_label {text-align:right; padding-bottom:.5em; padding-right:.5em;}"+
			"	.customer_td_input {text-align:left;}"+
			"	.text-field {width:16em;}"+
			"	#customer-panel p {margin-top:.75em; margin-bottom:.5em;}"+
			"	#customer-panel p span {font-size:.75em;}"+
			"	#city {width:13em;}"+
			"</style>"+
			"<div id='customer-panel'>\n"+
			"	<h2>Thank You For Your Order at <a href=\"https://the-little-store.herokuapp.com/\" target=\"_new\">The Little Store</a>!</h2>\n"+
			"	<h4 class='checkoutHeader'>Customer Details</h4>\n"+
			"	<table id='customer-table'>\n"+
			"		<tr>\n"+
			"			<td></td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='email'>Name:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_label'>\n"+
			"				"+customer.getFirstName()+" "+customer.getLastName()+"\n"+
			"			</td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='email'>Email:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+customer.getEmail()+"\n"+
			"			</td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='phone'>Phone:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+( customer.getPhone() == null || customer.getPhone().isEmpty() ? "(None Supplied)" : customer.getPhone() ) + "\n"+
			"			</td>\n"+
			"		</tr>\n"+
			"		<tr>\n"+
			"			<td colspan=2 class='customer_td_label'>\n"+
			"				<label for='address'>Meet-Up Address:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+( customer.getAddress()==null || customer.getAddress().isEmpty() ? "TBD" : customer.getAddress() ) +"\n"+
			"			</td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='city'>City:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+( customer.getCity()==null || customer.getCity().isEmpty() ? "TBD" : customer.getCity() ) +"\n"+
			"			</td>\n"+
			"			<td class='customer_td_label'>\n"+
			"				<label for='state'>State:</label>\n"+
			"			</td>\n"+
			"			<td class='customer_td_input'>\n"+
			"				"+listStates.get(customer.getState().ordinal())+"\n"+
			"			</td>\n"+
			"		</tr>\n"+
			"		<tr>\n"+
			"			<td colspan=2 class='customer_td_label'>\n"+
			"				<label for='paymentType'>Payment Type:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+listPayTypes.get(customer.getPreferredPayment().ordinal())+"\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_label'>\n"+
			"				<label for='paymentHandle'>Payment Handle:</label>\n"+
			"			</td>\n"+
			"			<td colspan=2 class='customer_td_input'>\n"+
			"				"+customer.getPaymentHandle()+"\n"+
			"			</td>\n"+
			"		</tr>\n"+
			"	</table>\n"+
			"</div>\n"+
			"<div id='checkout-panel'>\n"+
			"	<h4 class='checkoutHeader'>Order Details</h4>\n"+
			"	<div class='orderDetailHeader'><h4>\n"+
			"		<span>Order #"+customerOrder.getOrderNum()+"</span>\n"+
			"		<span>Order Date: "+
			"			"+customerOrder.getOrderDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE)+"\n"+
			"		</span>\n"+
			"		<span>Status: "+customerOrder.getStatus()+"</span>\n"+
			"	</h4></div>\n"+
			"	<table id='checkout-table'>\n"+
			"		<thead>\n"+
			"			<tr>\n"+
			"				<th></th>\n"+
			"				<th>Item</th>\n"+
			"				<th>Scent/Style</th>\n"+
			"				<th>Size</th>\n"+
			"				<th>Quantity</th>\n"+
			"				<th>Unit Price</th>\n"+
			"				<th>Subtotal</th>\n"+
			"			</tr>\n"+
			"		</thead>\n"+
			"		<tbody>\n";
			double orderTotal = 0;
			for (OrderDetail orderItem : customerOrder.getOrderItems()) {
				emailBody +=
			"			<tr>\n"+
			"				<td class='checkout_image_panel' style='background-color:white;'>"+
			"					<img style='height:2em;' src='"+orderItem.getProduct().getImage()+"' alt='"+orderItem.getProduct().getDescription()+"' />"+
			"				</td>\n"+
			"				<td>"+orderItem.getProduct().getName()+"</td>\n"+
			"				<td>"+orderItem.getProduct().getOptions()+"</td>\n"+
			"				<td>"+orderItem.getProduct().getSize()+"</td>\n"+
			"				<td align=\"center\">"+orderItem.getQty()+"</td>\n"+
			"				<td>"+String.format("$%,.2f", orderItem.getPrice())+"</td>\n"+
			"				<td>"+String.format("$%,.2f", orderItem.getQty() * orderItem.getPrice())+"</td>\n"+
			"			</tr>\n";
				orderTotal += orderItem.getQty() * orderItem.getPrice();
			}
			emailBody +=
			"		</tbody>\n"+
			"		<tfoot>\n"+
			"			<tr>\n"+
			"				<td  colspan=6 style='text-align:right;' class='checkout_subtotal_panel'>Total:</td>\n"+
			"				<td class='checkout_subtotal_panel'>"+String.format("$%,.2f", orderTotal)+"</td>\n"+
			"			</tr>\n"+
			"		</tfoot>\n"+
			"	</table>\n"+
			"</div>";
			new SendSimpleEmail(customer.getEmail(), 
					 "Little Store Order #"+customerOrder.getOrderNum()+" Confirmation",
					 emailBody);
			// Send order notification to my email
			new SendSimpleEmail("thelittlestoregoods@gmail.com", "New Order Received", emailBody);
			// Send order notification to printer
//			new SendSimpleEmail("jamitinmybox@hpeprint.com", "New Order Received", emailBody);

			
			model.addAttribute("customerInfo", customer);
			model.addAttribute("customerOrder", customerOrder);
			model.addAttribute("listStates", listStates);
			model.addAttribute("listPayTypes", listPayTypes);
			return "confirmation";
		}
	}

	
	
	
	@GetMapping("/admin")
	public String adminMainPage(Model model)
	{
		return "admin";
	}
	
	
	

/*	@RequestMapping({ "/buyProduct" })
	public String listProductHandler(HttpServletRequest request, Model model, //
			@RequestParam(value = "code", defaultValue = "") String code) {
 
		Product product = null;
		if (code != null && code.length() > 0) {
			product = productDAO.findProduct(code);
		}
		if (product != null) {
 
			//
			CartInfo cartInfo = Utils.getCartInSession(request);
 
			ProductInfo productInfo = new ProductInfo(product);
 
			cartInfo.addProduct(productInfo, 1);
		}
 
		return "redirect:/shoppingCart";
	}
 
	@RequestMapping({ "/shoppingCartRemoveProduct" })
	public String removeProductHandler(HttpServletRequest request, Model model, //
			@RequestParam(value = "code", defaultValue = "") String code) {
		Product product = null;
		if (code != null && code.length() > 0) {
			product = productDAO.findProduct(code);
		}
		if (product != null) {
 
			CartInfo cartInfo = Utils.getCartInSession(request);
 
			ProductInfo productInfo = new ProductInfo(product);
 
			cartInfo.removeProduct(productInfo);
 
		}
 
		return "redirect:/shoppingCart";
	}
 
	// POST: Update quantity for product in cart
	@RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.POST)
	public String shoppingCartUpdateQty(HttpServletRequest request, //
			Model model, //
			@ModelAttribute("cartForm") CartInfo cartForm) {
 
		CartInfo cartInfo = Utils.getCartInSession(request);
		cartInfo.updateQuantity(cartForm);
 
		return "redirect:/shoppingCart";
	}
 
	// GET: Show cart.
	@RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.GET)
	public String shoppingCartHandler(HttpServletRequest request, Model model) {
		CartInfo myCart = Utils.getCartInSession(request);
 
		model.addAttribute("cartForm", myCart);
		return "shoppingCart";
	}
 
	// GET: Enter customer information.
	@RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.GET)
	public String shoppingCartCustomerForm(HttpServletRequest request, Model model) {
 
		CartInfo cartInfo = Utils.getCartInSession(request);
 
		if (cartInfo.isEmpty()) {
 
			return "redirect:/shoppingCart";
		}
		CustomerInfo customerInfo = cartInfo.getCustomerInfo();
 
		CustomerForm customerForm = new CustomerForm(customerInfo);
 
		model.addAttribute("customerForm", customerForm);
 
		return "shoppingCartCustomer";
	}
 
	// POST: Save customer information.
	@RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.POST)
	public String shoppingCartCustomerSave(HttpServletRequest request, //
			Model model, //
			@ModelAttribute("customerForm") @Validated CustomerForm customerForm, //
			BindingResult result, //
			final RedirectAttributes redirectAttributes) {
 
		if (result.hasErrors()) {
			customerForm.setValid(false);
			// Forward to reenter customer info.
			return "shoppingCartCustomer";
		}
 
		customerForm.setValid(true);
		CartInfo cartInfo = Utils.getCartInSession(request);
		CustomerInfo customerInfo = new CustomerInfo(customerForm);
		cartInfo.setCustomerInfo(customerInfo);
 
		return "redirect:/shoppingCartConfirmation";
	}
 
	// GET: Show information to confirm.
	@RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.GET)
	public String shoppingCartConfirmationReview(HttpServletRequest request, Model model) {
		CartInfo cartInfo = Utils.getCartInSession(request);
 
		if (cartInfo == null || cartInfo.isEmpty()) {
 
			return "redirect:/shoppingCart";
		} else if (!cartInfo.isValidCustomer()) {
 
			return "redirect:/shoppingCartCustomer";
		}
		model.addAttribute("myCart", cartInfo);
 
		return "shoppingCartConfirmation";
	}
 
	// POST: Submit Cart (Save)
	@RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.POST)
 
	public String shoppingCartConfirmationSave(HttpServletRequest request, Model model) {
		CartInfo cartInfo = Utils.getCartInSession(request);
 
		if (cartInfo.isEmpty()) {
 
			return "redirect:/shoppingCart";
		} else if (!cartInfo.isValidCustomer()) {
 
			return "redirect:/shoppingCartCustomer";
		}
		try {
			orderDAO.saveOrder(cartInfo);
		} catch (Exception e) {
 
			return "shoppingCartConfirmation";
		}
 
		// Remove Cart from Session.
		Utils.removeCartInSession(request);
 
		// Store last cart.
		Utils.storeLastOrderedCartInSession(request, cartInfo);
 
		return "redirect:/shoppingCartFinalize";
	}
 
	@RequestMapping(value = { "/shoppingCartFinalize" }, method = RequestMethod.GET)
	public String shoppingCartFinalize(HttpServletRequest request, Model model) {
 
		CartInfo lastOrderedCart = Utils.getLastOrderedCartInSession(request);
 
		if (lastOrderedCart == null) {
			return "redirect:/shoppingCart";
		}
		model.addAttribute("lastOrderedCart", lastOrderedCart);
		return "shoppingCartFinalize";
	}
 
	@RequestMapping(value = { "/productImage" }, method = RequestMethod.GET)
	public void productImage(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("code") String code) throws IOException {
		Product product = null;
		if (code != null) {
			product = this.productDAO.findProduct(code);
		}
		if (product != null && product.getImage() != null) {
			response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			response.getOutputStream().write(product.getImage());
		}
		response.getOutputStream().close();
	}*/
}