package com.littlestore.controller.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.littlestore.config.GmailProperties;
import com.littlestore.controller.BaseController;
import com.littlestore.entity.Customer;
import com.littlestore.entity.Order;
import com.littlestore.entity.OrderStatus;
import com.littlestore.service.GmailEmailService;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController extends BaseController {

    public AdminOrderController(GmailEmailService emailService, GmailProperties gmailProps) {
        super(emailService, gmailProps);
    }

    @GetMapping
    public String listOrders(Model model, @ModelAttribute("message") String message,
            @ModelAttribute("error") String error) {
        model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
        model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
        model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
        List<Order> orders = orderService.listAll();
        Collections.reverse(orders);
        model.addAttribute("orders", orders);
        if (message != null && !message.isEmpty()) model.addAttribute("message", message);
        if (error != null && !error.isEmpty()) model.addAttribute("error", error);
        return "admin/orders/list";
    }

    @GetMapping("/{orderNum}/print")
    public String printOrder(Model model, @PathVariable(name = "orderNum") int orderNum) {
        Order customerOrder;
        try {
            customerOrder = orderService.get(orderNum);
        } catch (Exception e) {
            return "redirect:/admin/orders";
        }

        Collections.sort(customerOrder.getOrderItems());
        Customer customer = customerOrder.getCustomer();

        model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
        model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
        model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
        model.addAttribute("showRetailPrice", getGeneralDataInteger("showRetailPrice"));
        model.addAttribute("allowOosSearch", getGeneralDataInteger("allowOosSearch"));
        model.addAttribute("customerInfo", customer);
        model.addAttribute("customerOrder", customerOrder);
        model.addAttribute("listStates", listStates);
        model.addAttribute("listPayTypes", listPayTypes);
        model.addAttribute("listPaymentInfo", listPaymentInfo());
        return "admin/orders/print";
    }

    @GetMapping("/{orderNum}/resend")
    public String resendConfirmation(@PathVariable(name = "orderNum") int orderNum,
            RedirectAttributes redirectAttributes) {
        Order customerOrder;
        try {
            customerOrder = orderService.get(orderNum);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Order #" + orderNum + " not found.");
            return "redirect:/admin/orders";
        }

        Collections.sort(customerOrder.getOrderItems());
        Customer customer = customerOrder.getCustomer();

        String emailBody = null;
        try {
            emailBody = buildOrderConfirmationEmail(customer, customerOrder);
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Failed to build confirmation email for order #" + orderNum + ": " + e.getMessage());
            return "redirect:/admin/orders";
        }

        String to = customer.getEmail();
        String from = getGeneralDataString("senderEmail");
        String subject = "Little Store Order #" + customerOrder.getOrderNum() + " Confirmation";

        try {
            emailService.send(to, from, subject, emailBody);
            System.out.println("Resent order confirmation to " + to);
            redirectAttributes.addFlashAttribute("message",
                    "Order #" + orderNum + " confirmation resent to " + to + ".");
        } catch (Exception e) {
            System.err.println("Failed to resend confirmation: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Failed to send email for order #" + orderNum + ": " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }

    @PostMapping("/{orderNum}/status")
    public String updateStatus(@PathVariable(name = "orderNum") int orderNum,
            @RequestParam("status") OrderStatus status,
            RedirectAttributes redirectAttributes) {
        Order customerOrder;
        try {
            customerOrder = orderService.get(orderNum);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Order #" + orderNum + " not found.");
            return "redirect:/admin/orders";
        }
        customerOrder.setStatus(status);
        customerOrder.setStatusDateTime(java.time.LocalDateTime.now());
        orderService.save(customerOrder);
        redirectAttributes.addFlashAttribute("message",
                "Order #" + orderNum + " status updated to " + status.getLabel() + ".");
        return "redirect:/admin/orders";
    }

    @GetMapping("/{orderNum}/cancel")
    public String cancelOrder(@PathVariable(name = "orderNum") int orderNum,
            RedirectAttributes redirectAttributes) {
        Order customerOrder;
        try {
            customerOrder = orderService.get(orderNum);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Order #" + orderNum + " not found.");
            return "redirect:/admin/orders";
        }
        if (customerOrder.getStatus() == OrderStatus.DELIVERED) {
            redirectAttributes.addFlashAttribute("error",
                    "Order #" + orderNum + " is already delivered and cannot be cancelled.");
            return "redirect:/admin/orders";
        }
        customerOrder.setStatus(OrderStatus.CANCELLED);
        customerOrder.setStatusDateTime(java.time.LocalDateTime.now());
        orderService.save(customerOrder);
        redirectAttributes.addFlashAttribute("message", "Order #" + orderNum + " has been cancelled.");
        return "redirect:/admin/orders";
    }
}
