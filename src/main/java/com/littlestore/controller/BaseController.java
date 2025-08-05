package com.littlestore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.littlestore.config.GmailProperties;
import com.littlestore.entity.Customer;
import com.littlestore.entity.GeneralData;
import com.littlestore.entity.PaymentInfo;
import com.littlestore.service.CartDetailService;
import com.littlestore.service.CartService;
import com.littlestore.service.CustomerService;
import com.littlestore.service.EmailTemplateService;
import com.littlestore.service.GmailEmailService;
import com.littlestore.service.OrderDetailService;
import com.littlestore.service.OrderService;
import com.littlestore.service.ProductService;
import com.littlestore.service.GeneralDataService;
import com.littlestore.service.PaymentInfoService;
import com.littlestore.service.SecurityService;
import com.littlestore.validator.CustomerFormValidator;

public abstract class BaseController {
	@Autowired
	protected BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	protected CustomerService customerService;
	@Autowired
	protected ProductService productService;
	@Autowired
	protected CartService cartService;
	@Autowired
	protected CartDetailService cartDetailService;
	@Autowired
	protected OrderService orderService;
	@Autowired
	protected OrderDetailService orderDetailService;
	@Autowired
	protected GeneralDataService generalDataService;
	@Autowired
	protected PaymentInfoService paymentInfoService;
	@Autowired
	protected SecurityService securityService;
	@Autowired
	protected CustomerFormValidator customerFormValidator;
	@Autowired
	protected EmailTemplateService emailTemplateService;
	protected final GmailEmailService emailService;
    @Autowired
    protected SecurityService auth;
    
    protected final GmailProperties gmailProps;

    protected int hourDiffFromDb = 5;

	protected List<String> listStates = Stream.of(Customer.States.values()).map(Enum::name).collect(Collectors.toList());

	protected List<String> listPayTypes = Stream.of(Customer.PaymentMethods.values()).map(Enum::name)
			.collect(Collectors.toList());

    protected BaseController(GmailEmailService emailService, GmailProperties gmailProps) {
        this.emailService = emailService;
        this.gmailProps = gmailProps;
    }

    protected Customer getCurrentUser() {
    	return auth.getLoggedInUser();
    }

	public boolean currentUserIsAdmin() {
	    Authentication auth = SecurityContextHolder
	                              .getContext()
	                              .getAuthentication();
	    return auth.getAuthorities().stream()
	               .map(a -> a.getAuthority())
	               .anyMatch("ROLE_ADMIN"::equals);
	}

	protected List<GeneralData> getGeneralDataByCategory(String category) {
		return generalDataService.findByCategory(category);
	}

    protected Map<String, String> getGeneralDataCategoryMap(String category) {
		List<GeneralData> categoryItems = getGeneralDataByCategory(category);
		Map<String, String> generalDataMap = new HashMap<String, String>();
		for (GeneralData item : categoryItems) {
			generalDataMap.put(item.getGeneralName(), item.getGeneralValue());
		}
		return generalDataMap;
	}

    protected String getGeneralDataString(String generalName) {
		return generalDataService.getGeneralData(generalName);
	}

    protected int getGeneralDataInteger(String generalName) {
		String generalValue = getGeneralDataString(generalName);
		int generalInt = 0;
		try {
			generalInt = Integer.parseInt(generalValue);
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}
		return generalInt;
	}

    protected double getGeneralDataDouble(String generalName) {
		String generalValue = getGeneralDataString(generalName);
		double generalDouble = 0.0;
		try {
			generalDouble = Double.parseDouble(generalValue);
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}
		return generalDouble;
	}

    protected List<String> getNavMenuItems() {
		if (getGeneralDataInteger("showOosEverywhere") == 1) {
			return productService.listCategoryMain();
		} else {
			return productService.listCategoryMainWithStock();
		}
	}

    protected List<String> getNavSubMenuItems(String categoryName) {
		if (getGeneralDataInteger("showOosEverywhere") == 1) {
			return productService.listCategorySpecificUnderMain(categoryName);
		} else {
			return productService.listCategorySpecificUnderMainWithStock(categoryName);
		}
	}

    protected List<PaymentInfo> listPaymentInfo() {
		List<PaymentInfo> payTypes = paymentInfoService.listAll();
		return payTypes;
	}
}
