package com.littlestore.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.littlestore.config.GmailProperties;
import com.littlestore.controller.BaseController;
import com.littlestore.entity.Customer;
import com.littlestore.service.GmailEmailService;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {
  
	public AdminController(GmailEmailService emailService, GmailProperties gmailProps) {
		super(emailService, gmailProps);
	}

	@GetMapping({"", "/", "/dashboard"})
	public String ownerDashboard(Model model) {
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));

		Customer user = getCurrentUser();
		if (user == null)
			return "/login"; // If not logged in, redirect to login page
		else // Otherwise check Roles and direct to appropriate page
		{
			model.addAttribute("ownerUser", user);
			return "admin/dashboard";
		}
	}
}
