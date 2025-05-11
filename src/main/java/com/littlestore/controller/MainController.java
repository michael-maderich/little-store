package com.littlestore.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.littlestore.config.GmailProperties;
import com.littlestore.entity.Cart;
import com.littlestore.entity.CartDetail;
import com.littlestore.entity.Customer;
import com.littlestore.entity.Order;
import com.littlestore.entity.OrderDetail;
import com.littlestore.entity.Product;
import com.littlestore.service.EmailTemplateService;
import com.littlestore.service.GmailEmailService;
import com.littlestore.validator.PasswordResetValidator;

/**
 * @author Michael Maderich
 *
 */
@Controller
public class MainController extends BaseController {

	@Autowired PasswordResetValidator passwordReqLimiter;

	public MainController(GmailEmailService emailService, GmailProperties gmailProps) {
		super(emailService, gmailProps);
	}

	// Possibly update with optional argument that indicates link was /addtoCart and change message appropriately
	// to indicate something like "Available quantity changed to x. x added to cart." Something like that
	private String updateCartChanges() {
		String error = "";
		Customer customer = getCurrentUser();
		Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
		if (customerCart == null) { // If not cart, no changes
			return null;
		}
		List<CartDetail> cartItems = new ArrayList<>(customerCart.getCartItems());
		int i = 0;
		while (i < cartItems.size()) {
			Product checkedProduct = productService.get(cartItems.get(i).getProduct().getUpc());

			// Check for price differences
			float cartPrice = cartItems.get(i).getPrice();
			if (cartPrice != checkedProduct.getCurrentPrice()) {
				error += (error.isEmpty() ? "Notice: " : "") + "The price of " + checkedProduct.getDescription()
						+ " has changed from $" + String.format("%.2f", cartItems.get(i).getPrice()) + " to $"
						+ String.format("%.2f", checkedProduct.getCurrentPrice())
						+ "  since it was added to your cart.<br/>";
				cartItems.get(i).setPrice(checkedProduct.getCurrentPrice());
				cartDetailService.save(cartItems.get(i)); // Will overwrite any previous cartDetail with same composite key (cartId/upc)
			}
			// Adjust base price if changed; no notification to user necessary
			if (cartItems.get(i).getBasePrice() != checkedProduct.getBasePrice()) {
				cartItems.get(i).setBasePrice(checkedProduct.getBasePrice());
				cartDetailService.save(cartItems.get(i)); // Will overwrite any previous cartDetail with same composite key (cartId/upc)
			}

			// Check for stock availability changes
			int diff = cartItems.get(i).getQty() - checkedProduct.getStockQty();
			if (diff > 0) {
				if (checkedProduct.getStockQty() == 0) {
					CartDetail removedLineItem = cartDetailService.findLineByCartAndProduct(customerCart, cartItems.get(i).getProduct());
					cartItems.remove(removedLineItem);
					cartDetailService.delete(removedLineItem);
//					Collections.sort(cartItems);			// CartDetail entity contains compareTo() method
					i--; // Counteract i++ since list size has decreased
				} else {
					cartItems.get(i).setQty(checkedProduct.getStockQty()); // Set cart qty to available qty
					cartDetailService.save(cartItems.get(i)); // Will overwrite any previous cartDetail with same
																// composite key (cartId/upc)
				}
				error += (error.isEmpty() ? "Notice: " : "") + "The available qty of " + checkedProduct.getDescription()
						+ " has changed. " + diff + (diff == 1 ? " was" : " were") + " removed from your cart.<br/>";
			}
			i++;
		}
		customerCart.setCartItems(cartItems);
		if (cartItems.isEmpty())
			cartService.delete(customerCart); // If no cart items left available, we want creation time to reset
		else
			cartService.save(customerCart);
		/**/ System.out.println(customerCart);
		return error;
	}

	private Triple<String, String, String> getRandomTransparentImage() {
		List<Product> transparentImages = productService.getProductsWithTransparentImages();
		int index = (int) Math.floor(Math.random() * transparentImages.size());
		Product randomProduct = transparentImages.get(index);
		String image = randomProduct.getImage();
		String desc = randomProduct.getDescription();
		String productUrl = "/category/" + randomProduct.getCategoryMain() + "/" + randomProduct.getCategorySpecific()
				+ "/#" + randomProduct.getUpc();
		Triple<String, String, String> triplet = Triple.of(image, desc, productUrl);
		return triplet;
	}

    /**
    * Encodes a relative path for safe URL usage, preserving slashes.
    *
    * @param relativePath The relative path (e.g., "/images/Laundry/Dryer Sheets/037000823650.webp")
    * @return A fully URL-encoded path with slashes intact.
    */
    public static String encodePath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return "";
        }

        String[] parts = relativePath.split("/");
        List<String> encodedParts = new ArrayList<>();

        for (String part : parts) {
            if (!part.isEmpty()) { // Avoid encoding empty segments
                encodedParts.add(URLEncoder.encode(part, StandardCharsets.UTF_8));
            }
        }

        return "/" + String.join("/", encodedParts);
    }

    /**
    * Builds the full URL from a base domain and a relative path.
    *
    * @param baseUrl      The base URL (e.g., "https://the-little-store.herokuapp.com")
    * @param relativePath The relative path (e.g., "/images/Laundry/Dryer Sheets/037000823650.webp")
    * @return The full URL with proper encoding.
    */
    public static String buildFullUrl(String baseUrl, String relativePath) {
        return baseUrl + encodePath(relativePath);
    }

    // Build the email body
    public String buildOrderConfirmationEmail(Customer customer, Order order) throws IOException {
    	Map<String, String> variables = Map.ofEntries(
    		    Map.entry("firstName", customer.getFirstName()),
    		    Map.entry("lastName", customer.getLastName()),
    		    Map.entry("email", customer.getEmail()),
    		    Map.entry("phone", EmailTemplateService.safeString(customer.getPhone(), "(None Supplied)")),
    		    Map.entry("address", EmailTemplateService.safeString(customer.getAddress(), "TBD")),
    		    Map.entry("city", EmailTemplateService.safeString(customer.getCity(), "TBD")),
    		    Map.entry("state", (customer.getState() == null ? "TBD" : listStates.get(customer.getState().ordinal()))),
    		    Map.entry("payMethod", (customer.getPreferredPayment() == null ? "TBD" : listPayTypes.get(customer.getPreferredPayment().ordinal()))),
    		    Map.entry("payHandle", customer.getPaymentHandle() == null ? "" : customer.getPaymentHandle()),
    		    Map.entry("orderNum", String.valueOf(order.getOrderNum())),
    		    Map.entry("orderDate", order.getOrderDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE)),
    		    Map.entry("orderStatus", order.getStatus()),
    		    Map.entry("supportEmail", getGeneralDataString("receiverEmail")),
    		    Map.entry("orderItemsTable", buildOrderItemsTable(order))
    		);

        return emailTemplateService.loadTemplate("order-confirmation.html", variables);
    }

    // Helper method to build HTML table rows dynamically
    private String buildOrderItemsTable(Order order) {
    	String tableStyle = "margin:0 auto; margin-top: 2rem; border-collapse: collapse; width:100%";
    	String thStyle = "background-color: #f9f9f9; border:1px solid #ccc; padding: .5rem";
    	String tdStyle = "background-color: white; border:1px solid #ccc; padding: .5rem";

    	StringBuilder sb = new StringBuilder();
		sb.append("    <table id='checkout-table' style='"+ tableStyle + "'>");
		sb.append("        <thead>");
		sb.append("            <tr>");// style=\"font-weight:bold; background-color:#f0f0f0;\">");
		sb.append("                <th style ='"+thStyle+"'></th>");
		sb.append("                <th style ='"+thStyle+"; text-align:left'>Item</th>");
		sb.append("                <th style ='"+thStyle+"; text-align:left'>Scent/Style</th>");
		sb.append("                <th style ='"+thStyle+"; text-align:center'>Size</th>");
		sb.append("                <th style ='"+thStyle+"; text-align:center'>Quantity</th>");
		sb.append("                <th style ='"+thStyle+"; text-align:right'>Unit Price</th>");
		sb.append("                <th style ='"+thStyle+"; text-align:right'>Subtotal</th>");
		sb.append("            </tr>");
		sb.append("        </thead>");
		sb.append("        <tbody>");

		double orderTotal = 0;
		String urlRoot = getGeneralDataString("urlRoot");

		for (OrderDetail orderItem : order.getOrderItems()) {
			String relativeImgUrl = orderItem.getProduct().getImage();
			String fullImgUrl = buildFullUrl(urlRoot, relativeImgUrl);

			sb.append("            <tr>");
			sb.append("                <td class='checkout_image_panel' style='"+tdStyle+"; text-align:center'>");
			sb.append("                    <img style='height:2rem;' src='" + fullImgUrl + "' alt='" + orderItem.getProduct().getDescription() + "' />");
			sb.append("                </td>");
			sb.append("                <td style='"+tdStyle+"; text-align:left'>" + orderItem.getProduct().getName() + "</td>");
			sb.append("                <td style='"+tdStyle+"; text-align:left'>" + orderItem.getProduct().getOptions() + "</td>");
			sb.append("                <td style='"+tdStyle+"; text-align:center'>" + orderItem.getProduct().getSize() + "</td>");
			sb.append("                <td style='"+tdStyle+"; text-align: center'>" + orderItem.getQty() + "</td>");
			boolean itemOnSale = orderItem.getPrice() < orderItem.getBasePrice();
			sb.append("                <td style='"+tdStyle+"; text-align:right'>");
			if (itemOnSale) sb.append("<span style=\"color:green\">");
			sb.append(String.format("$%,.2f", orderItem.getPrice()));
			if (itemOnSale) sb.append("</span><br /><span style=\"text-decoration:line-through\">" + String.format("$%,.2f", orderItem.getBasePrice()) + "</span>");
			sb.append("                </td>");
			sb.append("                <td style='"+tdStyle+"; text-align:right'>" + String.format("$%,.2f", orderItem.getQty() * orderItem.getPrice()) + "</td>");
			sb.append("            </tr>");
			orderTotal += orderItem.getQty() * orderItem.getPrice();
		}
		sb.append("        </tbody>");
		sb.append("        <tfoot>");
		sb.append("            <tr style=\"font-weight:bold; background-color:#f0f0f0;\">");
		sb.append("                <td  colspan=6 style='text-align:right;' class='checkout_subtotal_panel'>Total:</td>");
		sb.append("                <td class='checkout_subtotal_panel' style='"+tdStyle+"; text-align:right; background-color:#f0f0f0'>" + String.format("$%,.2f", orderTotal) + "</td>");
		sb.append("            </tr>");
		sb.append("        </tfoot>");
		sb.append("    </table>");
		sb.append("</div>");
        return sb.toString();
    }

	@GetMapping("/403")
	public String accessDenied(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		return "/403";
	}

	@GetMapping("/{nonsense}")
	public String badUrl(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		return "redirect:/";
	}

	// Mapping to root/home/index page
	@GetMapping({ "/", "home", "/index" })
	public String home(Model model) {
		String cartAdjustments = null;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Customer customer = getCurrentUser();
		if (customer != null) { // If a User is logged in, get their cart, (or null if it doesn't exist)
			customer.setLastVisited(LocalDateTime.now().minusHours(hourDiffFromDb));
			customerService.update(customer);

			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
			}
		}
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("indexStyle", getGeneralDataString("indexStyle"));
		model.addAttribute("indexBgImageSrcDefault", getGeneralDataString("indexBgImageSrcDefault"));
		model.addAttribute("indexBgImageSrc", getGeneralDataString("indexBgImageSrc"));
		model.addAttribute("indexBgImageAltText", getGeneralDataString("indexBgImageAltText"));
		model.addAttribute("indexBgImageTooltip", getGeneralDataString("indexBgImageTooltip"));
		model.addAttribute("indexBgImageHyperlink", getGeneralDataString("indexBgImageHyperlink"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		return "/index";
	}

	@GetMapping("/login")
	public String login(Model model, HttpServletRequest request, String error, String logout) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		// in your controller
		String lastUsername = (String) request.getSession()
		                                      .getAttribute("SPRING_SECURITY_LAST_USERNAME");
		model.addAttribute("lastUsername", lastUsername);
		System.out.println(lastUsername);

		if (error != null) {
			model.addAttribute("error", "Your username and/or password is invalid.");
		}
		if (logout != null) {
			model.addAttribute("message", "You have been logged out successfully.");
		}
		return "/login"; // If not logged in, submit POST request to login page (handled by Spring Security)
	}

	@GetMapping("/signup")
	public String registration(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));

		if (getCurrentUser() != null)
			return "redirect:/account"; // If user is already signed in, redirect to account page.
		else {
			model.addAttribute("customerForm", new Customer());
			model.addAttribute("listStates", listStates);
			Triple<String, String, String> imageLeft = getRandomTransparentImage();
			model.addAttribute("transparentImageLeft", imageLeft);
			Triple<String, String, String> imageRight = getRandomTransparentImage();
			while (imageRight.equals(imageLeft)) {
				imageRight = getRandomTransparentImage();
			};
			model.addAttribute("transparentImageRight", imageRight);
			return "/signup";
		}
	}

	// When registration form is submitted, the signup page sends a POST request to itself.
	// The user info submitted is validated and return back to signup page if there are errors.
	// If the info submitted is valid, persist the customer data to the database
	@PostMapping("/signup")
	public String registration(Model model, @ModelAttribute("customerForm") Customer customerForm, BindingResult bindingResult) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);

		customerFormValidator.validate(customerForm, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("listStates", listStates); // States enum value list needs to be sent to signup page
														  // every time. I'm sure there's a better way to do this
			return "/signup";
		} else {
			customerForm.setIsEnabled(true);
			customerForm.setAccountCreated(LocalDateTime.now().minusHours(hourDiffFromDb));
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
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);
		Triple<String, String, String> imageBottom = getRandomTransparentImage();
		while (imageBottom.equals(imageLeft) || imageBottom.equals(imageRight)) {
			imageBottom = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageBottom", imageBottom);
		return "/forgotPassword";
	}

	@PostMapping("/forgotPassword")
	public String processForgotPassword(@RequestParam("email") String email,
										Model model, HttpServletRequest req,
										RedirectAttributes flash) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);
		Triple<String, String, String> imageBottom = getRandomTransparentImage();
		while (imageBottom.equals(imageLeft) || imageBottom.equals(imageRight)) {
			imageBottom = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageBottom", imageBottom);

		// send or reject the reset request and message user after page refresh
		Customer customer = customerService.findByEmail(email);
		String response = "If that email address exists in our system, you'll receive a reset link shortly.";

		// Prevent spamming
		String clientIp = req.getRemoteAddr();
		if (!passwordReqLimiter.allowForgotPassAttemptByIp(clientIp)) {
		    flash.addFlashAttribute("message", "Too many requests -- please wait a few minutes to retry.");
		    flash.addFlashAttribute("linkSent", true);
		    return "redirect:/forgotPassword";
		}
		// If they entered an invalid email..
		else if (customer == null) {
		    flash.addFlashAttribute("message", response);
		    flash.addFlashAttribute("linkSent", true);
		    // Do a 302 Redirect instead of rendering the JSP so refreshing the page can't resubmit password reset request
		    return "redirect:/forgotPassword";
	    }
	    else {
	    	// If link has been requested within 5 minutes, reject and let user know
	    	LocalDateTime lastReq = customer.getLastPasswordResetRequest();
	        if (lastReq != null && lastReq.isAfter(LocalDateTime.now().minusMinutes(5))) {
	            flash.addFlashAttribute("message",
	            						"A reset link was already sent recently.<br /><br />" +
        								"Please check your inbox or spam folder, or try again later."
	            );
	            flash.addFlashAttribute("linkSent", true);
	            return "redirect:/forgotPassword";
	        }
	        else {
		        // generate token & send email
		        String token = UUID.randomUUID().toString();
		        customer.setResetToken(token);
		        customer.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
	
		        String resetLink = getGeneralDataString("urlRoot") + "/resetPassword?token=" + token;
		        try {
					emailService.send(customer.getEmail(), getGeneralDataString("senderEmail"),
					    "Password Reset Request",
					    "Click the link below to reset your password. The link expires in 15 minutes.<br />" + resetLink);
			        // record the request time
			        customer.setLastPasswordResetRequest(LocalDateTime.now());
			        customerService.update(customer); // Save token + expiry
				} catch (MessagingException | IOException e) {
					e.printStackTrace();
			        model.addAttribute("error", "E-mail service is down.\nPlease try again later.");
			        return "/login";
				}
			    flash.addFlashAttribute("message", response);
			    flash.addFlashAttribute("linkSent", true);
			    // Do a 302 Redirect instead of rendering the JSP so refreshing the page can't resubmit password reset request
			    return "redirect:/forgotPassword";
	        }
	    }
	}
	
	@GetMapping("/resetPassword")
	public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);
		Triple<String, String, String> imageBottom = getRandomTransparentImage();
		while (imageBottom.equals(imageLeft) || imageBottom.equals(imageRight)) {
			imageBottom = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageBottom", imageBottom);

		Customer customer = customerService.findByResetToken(token);
	    if (customer == null || customer.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
	        model.addAttribute("error", "Invalid or expired token.<br />Please try Forgot Password again to get a new link.");
	        return "/login";
	    }
	    model.addAttribute("token", token);
	    return "resetPassword";
	}

	@PostMapping("/resetPassword")
	public String processResetPassword(
	    @RequestParam("token") String token,
	    @RequestParam("password") String password,
	    @RequestParam("confirmPassword") String confirmPassword,
	    Model model) {

		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);
		Triple<String, String, String> imageBottom = getRandomTransparentImage();
		while (imageBottom.equals(imageLeft) || imageBottom.equals(imageRight)) {
			imageBottom = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageBottom", imageBottom);

	    if (!password.equals(confirmPassword)) {
	        model.addAttribute("error", "Passwords do not match.");
	        model.addAttribute("token", token);
	        return "resetPassword";
	    }

	    Customer customer = customerService.findByResetToken(token);
	    if (customer == null || customer.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
	        model.addAttribute("error", "Invalid or expired token.<br />Please try Forgot Password again to get a new link.");
	        return "/login";
	    }

	    customer.setPassword(bCryptPasswordEncoder.encode(password));
	    customer.setResetToken(null);
	    customer.setResetTokenExpiry(null);
	    customerService.update(customer);

	    model.addAttribute("message", "Password reset successful. You may now log in.");
	    return "/login";  // or wherever you want
	}

	// When password reset form is submitted, the page sends a POST request to itself.
	// The user info submitted is validated and return back to signup page if there are errors.
	// If the info submitted is valid, persist the customer data to the database
	@PostMapping("/editAccount")
	public String editAccount(Model model, @ModelAttribute("customerForm") Customer customerForm, BindingResult bindingResult) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);
		Triple<String, String, String> imageBottom = getRandomTransparentImage();
		while (imageBottom.equals(imageLeft) || imageBottom.equals(imageRight)) {
			imageBottom = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageBottom", imageBottom);

		customerFormValidator.validatePasswordReset(customerForm, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("listStates", listStates); // States enum value list needs to be sent to signup page
															// every time. I'm sure there's a better way to do this
			return "/resetPassword";
		} else {
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
	public String encodeText(Model model, @PathVariable(name = "email") String email) {
		String encodedText = customerService.encrypt(email);
		model.addAttribute("encodedText", encodedText);
		return "/encode";
	}

	// Page for user to view/edit Profile, view Order History, and other functions TBD
	@GetMapping("/account")
	public String accountPage(Model model) {
//		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);

		Customer customer = getCurrentUser();
		if (customer == null) {
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("error", "You must be logged in to view your account.");
			return "/login";
		} else {
			int cartTotalItemQty = 0;
			float cartTotalItemCost = 0.0f;
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
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
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Customer customer = getCurrentUser();
		if (customer == null) { // Can't view orders if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("error", "You must be logged in to edit your account details.");
			return "/login";
		} else {
			int cartTotalItemQty = 0;
			float cartTotalItemCost = 0.0f;
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
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
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Triple<String, String, String> imageLeft = getRandomTransparentImage();
		model.addAttribute("transparentImageLeft", imageLeft);
		Triple<String, String, String> imageRight = getRandomTransparentImage();
		while (imageRight.equals(imageLeft)) {
			imageRight = getRandomTransparentImage();
		};
		model.addAttribute("transparentImageRight", imageRight);
		Customer customer = getCurrentUser();
		if (customer == null) { // Can't view orders if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("error", "You must be logged in to view your orders.");
			return "/login";
		} else {
			int cartTotalItemQty = 0;
			float cartTotalItemCost = 0.0f;
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
			}
			model.addAttribute("customer", customer);

			List<Order> orderList = orderService.findByCustomer(customer);
			for (Order order : orderList) {
				List<OrderDetail> orderItems = new ArrayList<>(order.getOrderItems());
				Collections.sort(orderItems); // OrderDetail entity contains compareTo() method. List sorted for better
												// order history display
				order.setOrderItems(orderItems);
//				orderService.save(order);
			}
			Collections.reverse(orderList); // Show most recent order first
			model.addAttribute("orderList", orderList);
			return "/order";
		}

	}

	@GetMapping("/category/")
	public String categoryRootRedirect() {
		return "redirect:/index";
	}

	@GetMapping("/category/{categoryName}")
	public String listItemsInCategory(Model model, @PathVariable(name = "categoryName") String categoryName,
			@RequestParam(value = "addedUpc", defaultValue = "") String addedUpc,
			@RequestParam(value = "addedItemQty", defaultValue = "0") String addedItemQty) {
		List<Product> itemList = !(categoryName.equalsIgnoreCase("Christmas Shop"))// ||
																					// categoryName.equalsIgnoreCase("Laundry"))
				? productService.findByCategoryMainMinQtySorted(categoryName, 0)
				/* List<Product> itemList = */ : productService.findByCategoryMainSorted(categoryName);
		String cartAdjustments = null;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		boolean goodLink = false;
		for (Product p : itemList)
			if (p.getCategoryMain().equals(categoryName))
				goodLink = true;
		if (!goodLink)
			return "redirect:/";

		Customer customer = getCurrentUser();
		if (customer != null) { // If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
				model.addAttribute("cartItems", cartItems);
			}
		}
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("navSubMenuItems", getNavSubMenuItems(categoryName));
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		return "category";
	}

	@GetMapping("/category/{categoryName}/{subCategoryName}")
	public String listItemsInSubCategory(@PathVariable(name = "categoryName") String categoryName,
			@PathVariable(name = "subCategoryName") String subCategoryName, Model model,
			@RequestParam(value = "addedUpc", defaultValue = "") String addedUpc,
			@RequestParam(value = "addedItemQty", defaultValue = "0") String addedItemQty) {
		List<Product> itemList = !(categoryName.equalsIgnoreCase("Christmas Shop"))// ||
																					// categoryName.equalsIgnoreCase("Laundry"))
				? productService.findByCategorySpecificMinQtySorted(subCategoryName, 0) // Filter out 0 qty items
				/* List<Product> itemList = */ : productService.findByCategorySpecificSorted(subCategoryName); // Shows
																												// 0 qty
																												// items
		String cartAdjustments = null;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		boolean goodLink = false;
		for (Product p : itemList)
			if (p.getCategoryMain().equals(categoryName) && p.getCategorySpecific().equals(subCategoryName))
				goodLink = true;
		if (!goodLink)
			return "redirect:/";

		Customer customer = getCurrentUser();
		if (customer != null) { // If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
				model.addAttribute("cartItems", cartItems);
			}
		}

		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("navSubMenuItems", getNavSubMenuItems(categoryName));
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		return "category";
	}

	@GetMapping("/images")
	public String showCombinedImages(Model model) {
		List<ArrayList<ArrayList<ArrayList<Product>>>> itemList = productService.findAllByCatAndSubcat(); // List<Cat<Subcat
																											// items>>

		model.addAttribute("allItems", itemList);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		return "images";
	}

	@GetMapping("/newitems")
	public String showNewItems(Model model, @RequestParam(value = "addedUpc", defaultValue = "") String addedUpc,
			@RequestParam(value = "addedItemQty", defaultValue = "0") String addedItemQty) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));

		String cartAdjustments = "";
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Customer customer = getCurrentUser();
		if (customer == null) { // Can't view new items if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("error",
					"Since items shown as new are based on your last order date, you must be logged in to view new items.");
			return "/login";
		} else { // If a User is logged in, get their cart, (or null if it doesn't exist)
			List<Product> itemList = productService.getNewItems(customer.getId());
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
				model.addAttribute("cartItems", cartItems);
			}
			model.addAttribute("cartAdjustments", cartAdjustments);
			model.addAttribute("addedUpc", addedUpc);
			model.addAttribute("addedItemQty", addedItemQty);
			model.addAttribute("itemList", itemList);
		}
		return "newitems";
	}

	@GetMapping("/dollarama")
	public String showDollarItems(Model model, @RequestParam(value = "addedUpc", defaultValue = "") String addedUpc,
			@RequestParam(value = "addedItemQty", defaultValue = "0") String addedItemQty) {
		String cartAdjustments = "";
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Customer customer = getCurrentUser();
		if (customer != null) { // If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
				model.addAttribute("cartItems", cartItems);
			}
		}
		List<Product> itemList = productService.getDollarItems();
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		return "dollarama";
	}

	@GetMapping("/sale")
	public String showSaleItems(Model model, @RequestParam(value = "addedUpc", defaultValue = "") String addedUpc,
			@RequestParam(value = "addedItemQty", defaultValue = "0") String addedItemQty) {
		String cartAdjustments = "";
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Customer customer = getCurrentUser();
		if (customer != null) { // If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
				model.addAttribute("cartItems", cartItems);
			}
		}
		List<Product> itemList = productService.getSaleItems();
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		return "sale";
	}

	@GetMapping("/search")
	public String itemSearch(Model model, @RequestParam(value = "q", defaultValue = "") String searchText,
			@RequestParam(value = "showOOS", defaultValue = "false", required = false) boolean showOOS,
			@RequestParam(value = "addedUpc", defaultValue = "") String addedUpc,
			@RequestParam(value = "addedItemQty", defaultValue = "0") String addedItemQty) {

		searchText = searchText.trim(); // Remove outer whitespace from search query
		List<Product> itemList = showOOS ? productService.getSearchResults(searchText)
				: productService.getSearchResultsWithStock(searchText);

		String cartAdjustments = null;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;

		Customer customer = getCurrentUser();
		if (customer != null) { // If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart != null) { // If they have a cart, fill cartItems with their cart item quantities
				cartAdjustments = updateCartChanges();
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}
				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
				model.addAttribute("cartItems", cartItems);
			}
		}
		model.addAttribute("cartAdjustments", cartAdjustments);
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		model.addAttribute("addedUpc", addedUpc);
		model.addAttribute("addedItemQty", addedItemQty);
		model.addAttribute("itemList", itemList);
		model.addAttribute("searchItemQty", itemList.size());
		model.addAttribute("searchText", searchText);
		return "searchresults";
	}

	@GetMapping("/addToCart")
	public String addItemsToCart(HttpServletRequest request, Model model,
			@RequestParam(value = "q", defaultValue = "") String searchText,
			@RequestParam(value = "upc", defaultValue = "") String upc,
			@RequestParam(value = "itemQty", defaultValue = "0") String itemQty) {

		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;

		String referer = request.getHeader("Referer"); // http://localhost:8080/xxxxxx - we just want the "xxxxxx"
		if (referer == null)
			return "redirect:/index"; // If page request didn't come from product page, reject and return to cart
		else {
			// grab everything after root '/', including the /
			referer = referer.substring(referer.indexOf('/', referer.indexOf('/') + 2));
			// remove the query string if exists
			referer = referer.substring(0, (referer.indexOf('?') != -1) ? referer.indexOf('?') : referer.length());
			if (!(referer.startsWith("/category") || referer.startsWith("/newitems") || referer.startsWith("/favorites")
					|| referer.startsWith("/dollarama") || referer.startsWith("/search")
					|| referer.startsWith("/sale")))
				return "redirect:" + referer;
		}

		Customer customer = getCurrentUser();
		Cart customerCart;
		Product purchasedProduct;
		int purchasedQty = Integer.parseInt(itemQty); // Can't throw exception because referrer string format already
														// checked
		if (purchasedQty < 0)
			purchasedQty = 0; // If somehow qty added is zero or negative - Mark >:(
		int addedItemQty = purchasedQty;
		if (addedItemQty <= 0) {
			return !referer.startsWith("/search")
					? "redirect:" + referer + "?addedUpc=" + upc + "&addedItemQty=" + addedItemQty + "#" + upc
					: "redirect:" + referer + "?q=" + searchText + "&addedUpc=" + upc + "&addedItemQty=" + addedItemQty
							+ "#" + upc;
		}
		try { // Irrelevant since referrer string checked, but maybe missed something
			purchasedProduct = productService.get(upc);
		} catch (NoSuchElementException e) {
			return "redirect:" + referer;
		}

		if (customer == null) { // Can't add to cart if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("navMenuItems", getNavMenuItems());
			model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
			model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
			model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
			model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
			model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
			model.addAttribute("error", "You must be logged in to add items to your cart.");
			return "/login";
		} else { // If a User is logged in, get their cart, (or null if it doesn't exist)
			customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null) { // If they don't have a cart started, start a new one
				customerCart = new Cart();
				customerCart.setCustomer(customer);
				customerCart.setCartCreationDateTime(LocalDateTime.now().minusHours(hourDiffFromDb));
				customerCart.setCartItems(new ArrayList<CartDetail>());
				cartService.save(customerCart); // New cart needs to be saved before items can be added because of
												// Foreign Key relationship
			}
			List<CartDetail> cartItems = new ArrayList<>(customerCart.getCartItems());
			int lineNum = 0;
			for (CartDetail cartItem : cartItems) {
				if (cartItem.getProduct().getUpc() == upc) { // One or more of this item is already in the cart, so just
																// increase qty
					purchasedQty += cartItem.getQty(); // Add qty already in cart to amount added to cart
					if (purchasedQty > cartItem.getProduct().getStockQty()) { // If more than available stock is
																				// requested..
						purchasedQty = cartItem.getProduct().getStockQty(); // Lower purchased qty to available stock
						addedItemQty = cartItem.getProduct().getStockQty() - cartItem.getQty(); // How many were
																								// actually added
					} else if (cartItem.getProduct().getPurchaseLimit() != 0
							&& purchasedQty > cartItem.getProduct().getPurchaseLimit()) { // If more than purchase limit
																							// (don't check 0!) is
																							// requested..
						purchasedQty = cartItem.getProduct().getPurchaseLimit(); // Lower purchased qty to purchase
																					// limit
						addedItemQty = cartItem.getProduct().getPurchaseLimit() - cartItem.getQty(); // How many were
																										// actually
																										// added
					}
//					item.setQty(item.getQty()+purchasedQty);		// Don't set now..
					cartItems.remove(cartItem); // delete and recreate instead for smoother code
					lineNum = cartItem.getLineNumber() - 1; // Get the item's line number -1 because will ++ after loop
					break; // out of foreach loop
				} else if (cartItem.getLineNumber() > lineNum)
					lineNum = cartItem.getLineNumber(); // Get new line number based on max existing
			}
			lineNum++;

			CartDetail newLineItem = new CartDetail(customerCart, purchasedProduct, purchasedQty,
					purchasedProduct.getRetailPrice(), purchasedProduct.getBasePrice(),
					purchasedProduct.getCurrentPrice(), lineNum);
			cartItems.add(newLineItem);
			Collections.sort(cartItems); // CartDetail entity contains compareTo() method. List sorted for better
											// cart/checkout display
			customerCart.setCartItems(cartItems);
			cartDetailService.save(newLineItem); // Will overwrite any previous cartDetail with same composite key
													// (cartId/upc)
			cartService.save(customerCart);
//			purchasedProduct.setStockQty(purchasedProduct.getStockQty()-addedItemQty);	// Remove items in carts from available qty
			/* System.out.println(customerCart); */

			for (CartDetail detail : cartItems) {
				cartTotalItemQty += detail.getQty();
				cartTotalItemCost += detail.getQty() * detail.getPrice();
			}
			model.addAttribute("cartTotalItemQty", cartTotalItemQty);
			model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
			model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
			model.addAttribute("customerCart", customerCart);
			model.addAttribute("searchText", searchText);
			model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
			model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
			return !referer.startsWith("/search")
					? "redirect:" + referer + "?addedUpc=" + upc + "&addedItemQty=" + addedItemQty + "#" + upc
					: "redirect:" + referer + "?q=" + searchText + "&addedUpc=" + upc + "&addedItemQty=" + addedItemQty
							+ "#" + upc;
		}
	}

	@GetMapping("/cart")
	public String cart(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		model.addAttribute("orderMinimum", getGeneralDataDouble("orderMinimum"));

		Customer customer = getCurrentUser();
		String cartAdjustments;
		Cart customerCart;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		if (customer == null) { // Can't view cart if not logged in, for now. Direct user to log in/sign up
			model.addAttribute("error", "You must be logged in to view your cart.");
			return "redirect:/login";
		} else { // If a User is logged in, get their cart, (or null if it doesn't exist)
			customerCart = cartService.findByCustomerEmail(customer.getEmail()); // Possibly null if no cart started,
																					// but handles fine
			if (customerCart == null) { // If they don't have a cart, redirect to cart page but couldn't get here unless
										// url typed/bookmarked
				model.addAttribute("customer", customer);
				model.addAttribute("customerCart", null);
				return "/cart";
			}
			// Update any items with stock or price changes
			cartAdjustments = updateCartChanges();
			List<CartDetail> cartItems = customerCart.getCartItems();
			Collections.sort(cartItems); // CartDetail entity contains compareTo() method
			customerCart.setCartItems(cartItems);
			for (CartDetail detail : cartItems) {
				cartTotalItemQty += detail.getQty();
				cartTotalItemCost += detail.getQty() * detail.getPrice();
			}
			model.addAttribute("cartTotalItemQty", cartTotalItemQty);
			model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
			model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
			model.addAttribute("customer", customer);
			model.addAttribute("customerCart", customerCart);
			model.addAttribute("cartAdjustments", cartAdjustments);
			return "/cart";
		}
	}

	@GetMapping("/removeFromCart")
	public String removeItemsFromCart(Model model, @RequestParam(value = "upc", defaultValue = "") String upc) {

		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		model.addAttribute("orderMinimum", getGeneralDataDouble("orderMinimum"));

		Customer customer = getCurrentUser();
		String cartAdjustments;
		Cart customerCart;
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;
		Product removedProduct;
		try { // This block only necessary if bad query string, which would only happen if url
				// entered manually
			removedProduct = productService.get(upc);
		} catch (NoSuchElementException e) {
			return "redirect:/cart";
		}
		if (customer == null) { // Can't edit cart if not logged in, but also can't get here since can't access
								// cart, either, unless url typed
			model.addAttribute("error", "You must be logged in to edit your cart.");
			return "/login";
		} else { // If a User is logged in, get their cart, (or null if it doesn't exist)
			customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null) { // Once again, only accessible through url through bad request
				return "redirect:/cart";
			}
			List<CartDetail> cartItems = new ArrayList<>(customerCart.getCartItems());
			CartDetail removedLineItem;
			try {
				removedLineItem = cartDetailService.findLineByCartAndProduct(customerCart, removedProduct);
			} catch (NoSuchElementException e) { // An item that does not exist in the cart has been attempted to be
													// removed, again manual URL
				return "redirect:/cart";
			}
			int removedQty = removedLineItem.getQty();
			cartAdjustments = ("" + removedQty).concat(" ").concat(removedLineItem.getProduct().getName()).concat(" ")
					.concat(removedLineItem.getProduct().getOptions()).concat(" ")
					.concat(removedLineItem.getProduct().getSize()).concat(" removed from cart");
			cartItems.remove(removedLineItem);
			Collections.sort(cartItems); // CartDetail entity contains compareTo() method
			customerCart.setCartItems(cartItems);
			cartDetailService.deleteLineByCartAndProduct(customerCart, removedProduct);
			if (cartItems.isEmpty())
				cartService.delete(customerCart); // If customer empties cart and comes back later, we want creation
													// time to reset
			else
				cartService.save(customerCart);
//			removedProduct.setStockQty(removedProduct.getStockQty() + removedLineItem.getQty());	// Return deleted items back to available stock
			/**/ System.out.println("Customer removed item from cart. " + customerCart);

			// After removal, check remaining cart items for stock/price changes
			String cartChanges = updateCartChanges();
			if (cartChanges != null && !cartChanges.isEmpty()) {
				if (cartAdjustments != null && !cartAdjustments.isEmpty()) {
					cartAdjustments += "<br />";
				}
				cartAdjustments += cartChanges;
			}
			cartItems = customerCart.getCartItems();
			Collections.sort(cartItems); // CartDetail entity contains compareTo() method
			customerCart.setCartItems(cartItems);

			for (CartDetail detail : cartItems) {
				cartTotalItemQty += detail.getQty();
				cartTotalItemCost += detail.getQty() * detail.getPrice();
			}

			model.addAttribute("cartTotalItemQty", cartTotalItemQty);
			model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
			model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
			model.addAttribute("customer", customer);
			model.addAttribute("customerCart", customerCart);
			model.addAttribute("cartAdjustments", cartAdjustments);
			return "/cart";
		}
	}

	@GetMapping("/clearCart")
	public String removeAllItemsFromCart(HttpServletRequest request, Model model) {

		String referer = request.getHeader("Referer"); // http://localhost:8080/xxxxxx - we just want the "xxxxxx"
		if (referer == null)
			return "redirect:/cart"; // If page request didn't come from the cart, reject it and return to cart
		else {
			referer = referer.substring(referer.indexOf('/', referer.indexOf("//") + 2)); // everything after root '/',
																							// inlcuding the /
			referer = referer.substring(0, (referer.indexOf('?') != -1) ? referer.indexOf('?') : referer.length()); // remove
																													// the
																													// query
																													// string
																													// if
																													// exists
			if (!referer.equals("/cart"))
				return "redirect:/cart";
		}

		Customer customer = getCurrentUser();
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Cart customerCart;
		if (customer == null) { // Can't delete cart if not logged in, but also can't get here since can't
								// access cart, either, unless url typed
			model.addAttribute("error", "You must be logged in to edit your cart.");
			return "/login";
		} else { // If a User is logged in, get their cart, (or null if it doesn't exist)
			customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null)
				return "redirect:/cart"; // Once again, only accessible through url through bad request, but handle it
			List<CartDetail> cartItems = new ArrayList<>(customerCart.getCartItems());
			for (CartDetail item : cartItems)
				cartDetailService.delete(item);
			cartService.delete(customerCart);
			model.addAttribute("cartTotalItemQty", 0);
			model.addAttribute("cartAdjustments", "Cart cleared");
			model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
			model.addAttribute("cartTotalItemCost", 0);
			model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
			model.addAttribute("customer", customer);
			return "/cart";
		}
	}

	@GetMapping("/checkout")
	public String orderFinalizationPage(Model model) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));

		String cartAdjustments = "";
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;

		Customer customer = getCurrentUser();
		if (customer == null) { // Can't check out if not logged in, for now. Direct user to log in
			model.addAttribute("error", "Please log in to your account to check out.");
			return "/login";
		} else { // If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null) { // If they don't have a cart, redirect to cart page but couldn't get here unless
										// url typed/bookmarked
				model.addAttribute("customer", customer);
				model.addAttribute("customerCart", customerCart);
				return "redirect:/cart";
			}
			// Adjust for any items that have changed stock or price
			cartAdjustments = updateCartChanges();
			// Reorganize cart so it's ordered by category/subcategory/name/options/size
			List<CartDetail> cartItems = customerCart.getCartItems();
			Collections.sort(cartItems); // CartDetail entity contains compareTo() method

			for (CartDetail detail : cartItems) {
				cartTotalItemQty += detail.getQty();
				cartTotalItemCost += detail.getQty() * detail.getPrice();
			}

			model.addAttribute("cartTotalItemQty", cartTotalItemQty);
			model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
			model.addAttribute("cartTotalItemCost", cartTotalItemCost);
			model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
			model.addAttribute("customerInfo", customer);
			model.addAttribute("customerCart", customerCart);
			model.addAttribute("cartAdjustments", cartAdjustments);

			double orderMinimum = getGeneralDataDouble("orderMinimum");
			if (cartTotalItemCost < orderMinimum) {
				model.addAttribute("orderMinimum", orderMinimum);
				model.addAttribute("error", "Minimum order total is " + String.format("$%,.2f", orderMinimum));
				return "redirect:/cart";
			} else {
				model.addAttribute("listStates", listStates);
				model.addAttribute("listPayTypes", listPayTypes);
				return "checkout";
			}
		}
	}

	@GetMapping("/connect")
	public void connect(HttpServletResponse response) throws IOException {
        String clientId    = gmailProps.getClientId();
        String redirectUri = gmailProps.getRedirectUri();
	    String scope = "https://www.googleapis.com/auth/gmail.send";

	    String oauthUrl = "https://accounts.google.com/o/oauth2/v2/auth"
	            + "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
	            + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
	            + "&response_type=code"
	            + "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8)
	            + "&access_type=offline"
	            + "&prompt=consent";

	    response.sendRedirect(oauthUrl);
	}

	@GetMapping("/oauth2/callback")
	public ResponseEntity<String> oauth2Callback(@RequestParam("code") String code) throws IOException {
        String clientId     = gmailProps.getClientId();
        String clientSecret = gmailProps.getClientSecret();
        String redirectUri  = gmailProps.getRedirectUri();
	    String tokenUrl = "https://oauth2.googleapis.com/token";

	    // Build the request body
	    String requestBody = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
	            + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
	            + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
	            + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
	            + "&grant_type=authorization_code";

	    // Send the POST request
	    HttpURLConnection conn = (HttpURLConnection) new URL(tokenUrl).openConnection();
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.setDoOutput(true);
	    try (OutputStream os = conn.getOutputStream()) {
	        os.write(requestBody.getBytes(StandardCharsets.UTF_8));
	    }

	    // Read the response
	    StringBuilder response;
	    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
	        response = new StringBuilder();
	        String line;
	        while ((line = br.readLine()) != null) {
	            response.append(line.trim());
	        }
	    }

	    // Print the response body
	    System.out.println("OAuth2 Token Response: " + response);

	    return ResponseEntity.ok("OAuth2 flow complete. Check your server logs for the token JSON.");
	}
	
    @PostMapping("/confirmation")
	public String completeOrder(Model model, @ModelAttribute("customerInfo") Customer customerUpdates) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));

		String cartAdjustments = "";
		int cartTotalItemQty = 0;
		float cartTotalItemCost = 0.0f;

		Customer customer = getCurrentUser();
		if (customer == null) { // Can't complete order if not logged in, for now. Direct user to log in page
			model.addAttribute("error", "Please log in to your account to check out.");
			return "/login";
		} else { // If a User is logged in, get their cart, (or null if it doesn't exist)
			Cart customerCart = cartService.findByCustomerEmail(customer.getEmail());
			if (customerCart == null) { // If they don't have a cart, redirect to cart page but couldn't get here unless
										// url typed/bookmarked
				model.addAttribute("customerInfo", customer);
				model.addAttribute("customerCart", customerCart);
				return "cart";
			}

			// Adjust for any items that have changed stock or price and return to checkout page if so
			cartAdjustments = updateCartChanges();
			if (cartAdjustments != null && !cartAdjustments.isEmpty()) {
				cartAdjustments += "<br />Please Check Out again.";

				// Get updated cart totals
				List<CartDetail> cartItems = customerCart.getCartItems();
				for (CartDetail detail : cartItems) {
					cartTotalItemQty += detail.getQty();
					cartTotalItemCost += detail.getQty() * detail.getPrice();
				}

				model.addAttribute("cartTotalItemQty", cartTotalItemQty);
				model.addAttribute("showItemQtyInHeader", getGeneralDataInteger("showItemQtyInHeader"));
				model.addAttribute("cartTotalItemCost", cartTotalItemCost);
				model.addAttribute("showTotalInHeader", getGeneralDataInteger("showTotalInHeader"));
				model.addAttribute("customerInfo", customer);
				model.addAttribute("customerCart", customerCart);
				model.addAttribute("cartAdjustments", cartAdjustments);
				model.addAttribute("orderMinimum", getGeneralDataDouble("orderMinimum"));
				return "cart";
			}

			// Add to Customer any updates to meeting address, phone/contact, payment method
			// and payment handle
			customer.setPhone(customerUpdates.getPhone().trim().isEmpty() ? null : customerUpdates.getPhone().trim());
			customer.setAddress(
					customerUpdates.getAddress().trim().isEmpty() ? null : customerUpdates.getAddress().trim());
			customer.setCity(customerUpdates.getCity().trim().isEmpty() ? null : customerUpdates.getCity().trim());
			customer.setState(customerUpdates.getState());
			customer.setPreferredPayment(customerUpdates.getPreferredPayment());
			customer.setPaymentHandle(customerUpdates.getPaymentHandle().trim().isEmpty() ? null
					: customerUpdates.getPaymentHandle().trim());
			customer.setLastOrdered(LocalDateTime.now().minusHours(hourDiffFromDb)); // Set last order time for
																						// determining New Items
			customerService.update(customer);

			// Convert cart to Order and delete Cart
			Order customerOrder = new Order();
			customerOrder.setCustomer(customer);
			customerOrder.setOrderDateTime(LocalDateTime.now().minusHours(hourDiffFromDb));
			customerOrder.setReqDeliveryDateTime(null);
			customerOrder.setStatus("Confirmed"); // Will need to update this later using enum
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
				lineItem.setProduct(item.getProduct()); // (product);
				lineItem.setDescription(item.getProduct().getDescription());
				lineItem.setImage(item.getProduct().getImage());
				lineItem.setQty(item.getQty());
				lineItem.setQtyFulfilled(item.getQty()); // Set fulfilled qty to ordered qty by default
				lineItem.setRetailPrice(item.getRetailPrice());
				lineItem.setBasePrice(item.getBasePrice());
				lineItem.setPrice(item.getPrice());
				lineItem.setLineNumber(lineNum++);
				orderItems.add(lineItem);
				orderDetailService.save(lineItem); // Save Order line item
				cartDetailService.delete(item); // Delete item from CartDetail table
				// Remove purchased qty from database
				String upc = item.getProduct().getUpc();
				Product product = productService.get(upc);
				product.setStockQty(product.getStockQty() - item.getQty());
				product.setDateLastSold(LocalDateTime.now().minusHours(hourDiffFromDb));
				productService.save(product);
			}
			customerOrder.setOrderItems(orderItems);
			orderService.save(customerOrder);
			cartService.delete(customerCart); // Remove the cart from DB

			// Craft order confirmation to be sent to customer's email address
			String emailBody = null;
			try {
				emailBody = buildOrderConfirmationEmail(customer, customerOrder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        String to = customer.getEmail();
	        String from = getGeneralDataString("senderEmail");
	        String subject = "Little Store Order #" + customerOrder.getOrderNum() + " Confirmation";

	        if (emailBody != null) {
		        try {
					emailService.send(to, from, subject, emailBody);
		            System.out.println("Email sent to " + to);
		        } catch (Exception e) {
		            System.err.println("Failed to send email: " + e.getMessage());
		            model.addAttribute("error", e.getMessage());
		        }
	        }

	        model.addAttribute("customerInfo", customer);
			model.addAttribute("customerOrder", customerOrder);
			model.addAttribute("listStates", listStates);
			model.addAttribute("listPayTypes", listPayTypes);
			model.addAttribute("listPaymentInfo", listPaymentInfo());
			return "confirmation";
		}
	}

	@GetMapping("/resendConfirmation/{email}/{orderNum}")
	public String resendOrderConfirmation(Model model, @PathVariable(name = "email") String email,
			@PathVariable(name = "orderNum") String orderNum) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));

		Customer customer = customerService.findByEmail(email);
		if (customer == null) { // Can't resend confirmation if user doesn't exist
			model.addAttribute("error", "User email not found.");
			return "/login";
		} else {
			Order customerOrder = null;
			try {
				customerOrder = orderService.get(Integer.parseInt(orderNum));
			} catch (Exception e) {
				model.addAttribute("error", "Order number not found for user " + email + ".");
				return "/login";
			}

			if (customerOrder.getCustomer().getId() != customer.getId()) {
				model.addAttribute("error", "Order number not found for user " + email + ".");
				return "/login";
			}

			// Add each Order Detail to Order Detail table
			List<OrderDetail> orderItems = customerOrder.getOrderItems();
			Collections.sort(orderItems);

			// Craft order confirmation to be sent to customer's email address
			String emailBody = null;
			try {
				emailBody = buildOrderConfirmationEmail(customer, customerOrder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        String to = customer.getEmail();
	        String from = getGeneralDataString("senderEmail");
	        String subject = "Little Store Order #" + customerOrder.getOrderNum() + " Confirmation";

	        if (emailBody != null) {
		        try {
					emailService.send(to, from, subject, emailBody);
		            System.out.println("Email sent to " + to);
		        } catch (Exception e) {
		            System.err.println("Failed to send email: " + e.getMessage());
		            // optionally log or rethrow
		        }
	        }

	        model.addAttribute("customerInfo", customer);
			model.addAttribute("customerOrder", customerOrder);
			model.addAttribute("listStates", listStates);
			model.addAttribute("listPayTypes", listPayTypes);
			model.addAttribute("listPaymentInfo", listPaymentInfo());
			return "confirmation";
		}
	}

	@GetMapping("/printOrder/{email}/{orderNum}")
	public String printOrder(Model model, @PathVariable(name = "email") String email,
			@PathVariable(name = "orderNum") String orderNum) {
		model.addAttribute("navMenuItems", getNavMenuItems());
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
		model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
		Customer customer = customerService.findByEmail(email);
		if (customer == null) { // Can't print order if user doesn't exist
			model.addAttribute("error", "User email not found.");
			return "/login";
		} else {
			Order customerOrder = null;
			try {
				customerOrder = orderService.get(Integer.parseInt(orderNum));
			} catch (Exception e) {
				model.addAttribute("error", "Order number not found for user " + email + ".");
				return "/login";
			}

			if (customerOrder.getCustomer().getId() != customer.getId()) {
				model.addAttribute("error", "Order number not found for user " + email + ".");
				return "/login";
			}

			// Add each Order Detail to Order Detail table
			List<OrderDetail> orderItems = customerOrder.getOrderItems();
			Collections.sort(orderItems);

			model.addAttribute("customerInfo", customer);
			model.addAttribute("customerOrder", customerOrder);
			model.addAttribute("listStates", listStates);
			model.addAttribute("listPayTypes", listPayTypes);
			model.addAttribute("listPaymentInfo", listPaymentInfo());
			return "printOrder";
		}
	}

	/*
	 * @RequestMapping({ "/buyProduct" }) public String
	 * listProductHandler(HttpServletRequest request, Model model, //
	 * 
	 * @RequestParam(value = "code", defaultValue = "") String code) {
	 * 
	 * Product product = null; if (code != null && code.length() > 0) { product =
	 * productDAO.findProduct(code); } if (product != null) {
	 * 
	 * // CartInfo cartInfo = Utils.getCartInSession(request);
	 * 
	 * ProductInfo productInfo = new ProductInfo(product);
	 * 
	 * cartInfo.addProduct(productInfo, 1); }
	 * 
	 * return "redirect:/shoppingCart"; }
	 * 
	 * @RequestMapping({ "/shoppingCartRemoveProduct" }) public String
	 * removeProductHandler(HttpServletRequest request, Model model, //
	 * 
	 * @RequestParam(value = "code", defaultValue = "") String code) { Product
	 * product = null; if (code != null && code.length() > 0) { product =
	 * productDAO.findProduct(code); } if (product != null) {
	 * 
	 * CartInfo cartInfo = Utils.getCartInSession(request);
	 * 
	 * ProductInfo productInfo = new ProductInfo(product);
	 * 
	 * cartInfo.removeProduct(productInfo);
	 * 
	 * }
	 * 
	 * return "redirect:/shoppingCart"; }
	 * 
	 * // POST: Update quantity for product in cart
	 * 
	 * @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.POST)
	 * public String shoppingCartUpdateQty(HttpServletRequest request, // Model
	 * model, //
	 * 
	 * @ModelAttribute("cartForm") CartInfo cartForm) {
	 * 
	 * CartInfo cartInfo = Utils.getCartInSession(request);
	 * cartInfo.updateQuantity(cartForm);
	 * 
	 * return "redirect:/shoppingCart"; }
	 * 
	 * // GET: Show cart.
	 * 
	 * @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.GET)
	 * public String shoppingCartHandler(HttpServletRequest request, Model model) {
	 * CartInfo myCart = Utils.getCartInSession(request);
	 * 
	 * model.addAttribute("cartForm", myCart); return "shoppingCart"; }
	 * 
	 * // GET: Enter customer information.
	 * 
	 * @RequestMapping(value = { "/shoppingCartCustomer" }, method =
	 * RequestMethod.GET) public String shoppingCartCustomerForm(HttpServletRequest
	 * request, Model model) {
	 * 
	 * CartInfo cartInfo = Utils.getCartInSession(request);
	 * 
	 * if (cartInfo.isEmpty()) {
	 * 
	 * return "redirect:/shoppingCart"; } CustomerInfo customerInfo =
	 * cartInfo.getCustomerInfo();
	 * 
	 * CustomerForm customerForm = new CustomerForm(customerInfo);
	 * 
	 * model.addAttribute("customerForm", customerForm);
	 * 
	 * return "shoppingCartCustomer"; }
	 * 
	 * // POST: Save customer information.
	 * 
	 * @RequestMapping(value = { "/shoppingCartCustomer" }, method =
	 * RequestMethod.POST) public String shoppingCartCustomerSave(HttpServletRequest
	 * request, // Model model, //
	 * 
	 * @ModelAttribute("customerForm") @Validated CustomerForm customerForm, //
	 * BindingResult result, // final RedirectAttributes redirectAttributes) {
	 * 
	 * if (result.hasErrors()) { customerForm.setValid(false); // Forward to reenter
	 * customer info. return "shoppingCartCustomer"; }
	 * 
	 * customerForm.setValid(true); CartInfo cartInfo =
	 * Utils.getCartInSession(request); CustomerInfo customerInfo = new
	 * CustomerInfo(customerForm); cartInfo.setCustomerInfo(customerInfo);
	 * 
	 * return "redirect:/shoppingCartConfirmation"; }
	 * 
	 * // GET: Show information to confirm.
	 * 
	 * @RequestMapping(value = { "/shoppingCartConfirmation" }, method =
	 * RequestMethod.GET) public String
	 * shoppingCartConfirmationReview(HttpServletRequest request, Model model) {
	 * CartInfo cartInfo = Utils.getCartInSession(request);
	 * 
	 * if (cartInfo == null || cartInfo.isEmpty()) {
	 * 
	 * return "redirect:/shoppingCart"; } else if (!cartInfo.isValidCustomer()) {
	 * 
	 * return "redirect:/shoppingCartCustomer"; } model.addAttribute("myCart",
	 * cartInfo);
	 * 
	 * return "shoppingCartConfirmation"; }
	 * 
	 * // POST: Submit Cart (Save)
	 * 
	 * @RequestMapping(value = { "/shoppingCartConfirmation" }, method =
	 * RequestMethod.POST)
	 * 
	 * public String shoppingCartConfirmationSave(HttpServletRequest request, Model
	 * model) { CartInfo cartInfo = Utils.getCartInSession(request);
	 * 
	 * if (cartInfo.isEmpty()) {
	 * 
	 * return "redirect:/shoppingCart"; } else if (!cartInfo.isValidCustomer()) {
	 * 
	 * return "redirect:/shoppingCartCustomer"; } try {
	 * orderDAO.saveOrder(cartInfo); } catch (Exception e) {
	 * 
	 * return "shoppingCartConfirmation"; }
	 * 
	 * // Remove Cart from Session. Utils.removeCartInSession(request);
	 * 
	 * // Store last cart. Utils.storeLastOrderedCartInSession(request, cartInfo);
	 * 
	 * return "redirect:/shoppingCartFinalize"; }
	 * 
	 * @RequestMapping(value = { "/shoppingCartFinalize" }, method =
	 * RequestMethod.GET) public String shoppingCartFinalize(HttpServletRequest
	 * request, Model model) {
	 * 
	 * CartInfo lastOrderedCart = Utils.getLastOrderedCartInSession(request);
	 * 
	 * if (lastOrderedCart == null) { return "redirect:/shoppingCart"; }
	 * model.addAttribute("lastOrderedCart", lastOrderedCart); return
	 * "shoppingCartFinalize"; }
	 * 
	 * @RequestMapping(value = { "/productImage" }, method = RequestMethod.GET)
	 * public void productImage(HttpServletRequest request, HttpServletResponse
	 * response, Model model,
	 * 
	 * @RequestParam("code") String code) throws IOException { Product product =
	 * null; if (code != null) { product = this.productDAO.findProduct(code); } if
	 * (product != null && product.getImage() != null) {
	 * response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
	 * response.getOutputStream().write(product.getImage()); }
	 * response.getOutputStream().close(); }
	 */
}