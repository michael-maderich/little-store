package com.littlestore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.littlestore.entity.Customer;
import com.littlestore.service.GmailEmailService;

@Controller
@RequestMapping("/admin")
public class OwnerController extends BaseController {
  
	public OwnerController(GmailEmailService emailService) {
		super(emailService);
	}

	@GetMapping("/dashboard")
	public String adminMainPage(Model model) {
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		Customer user = getCurrentUser();
		if (user == null)
			return "/login"; // If not logged in, redirect to login page
		else // Otherwise check Roles and direct to appropriate page
		{
			return "admin/dashboard";
		}
	}

    // e.g. “Manage Orders”
    @GetMapping("/orders")
    public String listOrders( Model m ) {
    	m.addAttribute("orders", orderService.listAll() );
    	return "admin/orders";
    }
  
    // e.g. “Manage Products”
    @GetMapping("/products")
    public String listProducts(Model m) {
    	m.addAttribute("products", productService.listAll());
    	return "admin/products";
    }
  
    // etc…
}
