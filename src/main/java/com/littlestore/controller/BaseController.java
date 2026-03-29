package com.littlestore.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.littlestore.entity.Order;
import com.littlestore.entity.OrderDetail;
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

    /**
     * Encodes a relative path for safe URL usage, preserving slashes.
     */
    public static String encodePath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return "";
        }
        String[] parts = relativePath.split("/");
        List<String> encodedParts = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {	// Avoid encoding empty segments which would turn into %2F
                encodedParts.add(URLEncoder.encode(part, StandardCharsets.UTF_8));
            }
        }
        return "/" + String.join("/", encodedParts);
    }

    /**
     * Builds the full URL from a base domain and a relative path.
     */
    public static String buildFullUrl(String baseUrl, String relativePath) {
        return baseUrl + encodePath(relativePath);
    }

    /**
     * Builds the HTML email body for an order confirmation.
     */
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

    private String buildOrderItemsTable(Order order) {
    	String tableStyle = "margin:0 auto; margin-top: 2rem; border-collapse: collapse; width:100%";
    	String thStyle = "background-color: #f9f9f9; border:1px solid #ccc; padding: .5rem";
    	String tdStyle = "background-color: white; border:1px solid #ccc; padding: .5rem";

    	StringBuilder sb = new StringBuilder();
		sb.append("    <table id='checkout-table' style='"+ tableStyle + "'>");
		sb.append("        <thead>");
		sb.append("            <tr>");
		sb.append("                <th style ='"+ thStyle +"'></th>");
		sb.append("                <th style ='"+ thStyle +"; text-align:left'>Item</th>");
		sb.append("                <th style ='"+ thStyle +"; text-align:left'>Scent/Style</th>");
		sb.append("                <th style ='"+ thStyle +"; text-align:center'>Size</th>");
		sb.append("                <th style ='"+ thStyle +"; text-align:center'>Quantity</th>");
		sb.append("                <th style ='"+ thStyle +"; text-align:right'>Unit Price</th>");
		sb.append("                <th style ='"+ thStyle +"; text-align:right'>Subtotal</th>");
		sb.append("            </tr>");
		sb.append("        </thead>");
		sb.append("        <tbody>");

		double orderTotal = 0;
		String urlRoot = getGeneralDataString("urlRoot");

		for (OrderDetail orderItem : order.getOrderItems()) {
			String relativeImgUrl = orderItem.getProduct().getImage();
			String fullImgUrl = buildFullUrl(urlRoot, relativeImgUrl);

			sb.append("            <tr>");
			sb.append("                <td class='checkout_image_panel' style='"+ tdStyle +"; text-align:center'>");
			sb.append("                    <img style='height:2rem;' src='" + fullImgUrl + "' alt='" + orderItem.getProduct().getDescription() + "' />");
			sb.append("                </td>");
			sb.append("                <td style='"+ tdStyle +"; text-align:left'>" + orderItem.getProduct().getName() + "</td>");
			sb.append("                <td style='"+ tdStyle +"; text-align:left'>" + orderItem.getProduct().getOptions() + "</td>");
			sb.append("                <td style='"+ tdStyle +"; text-align:center'>" + orderItem.getProduct().getSize() + "</td>");
			sb.append("                <td style='"+ tdStyle +"; text-align: center'>" + orderItem.getQty() + "</td>");
			boolean itemOnSale = orderItem.getPrice() < orderItem.getBasePrice();
			sb.append("                <td style='"+ tdStyle +"; text-align:right'>");
			if (itemOnSale) sb.append("<span style=\"color:green\">");
			sb.append(String.format("$%,.2f", orderItem.getPrice()));
			if (itemOnSale) sb.append("</span><br /><span style=\"text-decoration:line-through\">" + String.format("$%,.2f", orderItem.getBasePrice()) + "</span>");
			sb.append("                </td>");
			sb.append("                <td style='"+ tdStyle +"; text-align:right'>" + String.format("$%,.2f", orderItem.getQty() * orderItem.getPrice()) + "</td>");
			sb.append("            </tr>");
			orderTotal += orderItem.getQty() * orderItem.getPrice();
		}
		sb.append("        </tbody>");
		sb.append("        <tfoot>");
		sb.append("            <tr style=\"font-weight:bold; background-color:#f0f0f0;\">");
		sb.append("                <td  colspan=6 style='text-align:right;' class='checkout_subtotal_panel'>Total:</td>");
		sb.append("                <td class='checkout_subtotal_panel' style='"+ tdStyle +"; text-align:right; background-color:#f0f0f0'>" + String.format("$%,.2f", orderTotal) + "</td>");
		sb.append("            </tr>");
		sb.append("        </tfoot>");
		sb.append("    </table>");
		sb.append("</div>");
        return sb.toString();
    }
}
