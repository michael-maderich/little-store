package com.littlestore.controller.admin;

import com.littlestore.config.GmailProperties;
import com.littlestore.controller.BaseController;
import com.littlestore.entity.Product;
import com.littlestore.service.ProductService;
import com.littlestore.service.GmailEmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import javax.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController extends BaseController {

    private final ProductService productService;
    private final Cloudinary cloudinary;

    public AdminProductController(GmailEmailService emailService, GmailProperties gmailProps, ProductService productService, Cloudinary cloudinary) {
		super(emailService, gmailProps);
        this.productService = productService;
        this.cloudinary = cloudinary;
    }

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.listAll();
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
        model.addAttribute("products", products);
        return "admin/products/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
        List<String> allCategoryMain      = productService.listCategoryMain();
        List<String> allCategorySecondary = productService.listCategoryMain();
        List<String> allCategorySpecific  = productService.listCategorySpecific();

        model.addAttribute("allCategoryMain", allCategoryMain);
        model.addAttribute("allCategorySecondary", allCategorySecondary);
        model.addAttribute("allCategorySpecific", allCategorySpecific);
        model.addAttribute("product", new Product());
        return "admin/products/form";
    }

    @GetMapping("/edit/{upc}")
    public String showEditForm(@PathVariable String upc, Model model) {
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));

		Product product = productService.get(upc);
        if (product == null) {
            // If no product found, redirect to list with error
            model.addAttribute("errorMessage", "No such product: " + upc);
            return "redirect:/admin/products";
        }

        List<String> allCategoryMain      = productService.listCategoryMain();
        List<String> allCategorySecondary = productService.listCategoryMain();
        List<String> allCategorySpecific  = productService.listCategorySpecific();

        model.addAttribute("allCategoryMain", allCategoryMain);
        model.addAttribute("allCategorySecondary", allCategorySecondary);
        model.addAttribute("allCategorySpecific", allCategorySpecific);
        model.addAttribute("product", product);
        return "admin/products/form";
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute Product product,
    						  BindingResult bindingResult,
            				  @RequestParam("imageFile") MultipartFile imageFile,
                              RedirectAttributes redirect,
                              Model model) {

        boolean isNew = (product.getUpc() == null || product.getUpc().trim().isEmpty());

        // ─────────────────────────────────────────────────────────────
        // A) Check required fields via Bean Validation
        // ─────────────────────────────────────────────────────────────
        if (bindingResult.hasErrors()) {
            // Re-inject dropdown lists, so form.jsp can re-render them
            model.addAttribute("allCategoryMain",      productService.listCategoryMain());
            model.addAttribute("allCategorySecondary", productService.listCategoryMain());
            model.addAttribute("allCategorySpecific",  productService.listCategorySpecific());
            return "admin/products/form";
        }

        // ─────────────────────────────────────────────────────────────
        // B) UPC uniqueness check (only for create)
        // ─────────────────────────────────────────────────────────────
        if (isNew) {
            if (productService.get(product.getUpc())!=null) {
                bindingResult.rejectValue("upc", "error.product", "UPC must be unique");
                model.addAttribute("allCategoryMain",      productService.listCategoryMain());
                model.addAttribute("allCategorySecondary", productService.listCategoryMain());
                model.addAttribute("allCategorySpecific",  productService.listCategorySpecific());
                return "admin/products/form";
            }
        }

        // ─────────────────────────────────────────────────────────────
        // C) Handle image upload if a new file was provided
        // ─────────────────────────────────────────────────────────────
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    imageFile.getBytes(),
                    ObjectUtils.asMap(
                        "folder", "littlestore/products",
                        "resource_type", "image"
                    )
                );
                String secureUrl = (String) uploadResult.get("secure_url");
                product.setImage(secureUrl);
            } catch (IOException ioex) {
                bindingResult.rejectValue("imageUrl", "error.product", "Image upload failed");
                model.addAttribute("allCategoryMain",      productService.listCategoryMain());
                model.addAttribute("allCategorySecondary", productService.listCategoryMain());
                model.addAttribute("allCategorySpecific",  productService.listCategorySpecific());
                return "admin/products/form";
            }
        } else if (isNew) {
            // If creating and no image was uploaded, force an error
            bindingResult.rejectValue("imageUrl", "error.product", "Image is required");
            model.addAttribute("allCategoryMain",      productService.listCategoryMain());
            model.addAttribute("allCategorySecondary", productService.listCategoryMain());
            model.addAttribute("allCategorySpecific",  productService.listCategorySpecific());
            return "admin/products/form";
        }
        // If editing and they didn’t choose a new file, we leave product.getImageUrl() as-is.

        // ─────────────────────────────────────────────────────────────
        // D) Build the computed fields
        // ─────────────────────────────────────────────────────────────

        // 1) description = name + " " + options + " " + size
        String desc = product.getName();
        if (product.getOptions() != null && !product.getOptions().trim().isEmpty()) {
            desc += " " + product.getOptions().trim();
        }
        if (product.getSize() != null && !product.getSize().trim().isEmpty()) {
            desc += " " + product.getSize().trim();
        }
        product.setDescription(desc);

        // 2) onSale = (currentPrice < basePrice)
        product.setOnSale(product.getCurrentPrice() < product.getBasePrice());

        // 3) dateAdded: set now if new, otherwise keep existing
        if (isNew) {
            product.setDateAdded(LocalDateTime.now());
        } else {
            // Fetch existing product so we don’t overwrite dateAdded/dateLastSold
            Product existing = productService.get(product.getUpc());
            if (existing != null) {
                product.setDateAdded(existing.getDateAdded());
                product.setDateLastSold(existing.getDateLastSold());
            }
        }

        // 4) inventoried/inventoriedDate – leave at default (0 / null) since not exposed

        // ─────────────────────────────────────────────────────────────
        // E) Persist and redirect
        // ─────────────────────────────────────────────────────────────
        productService.save(product);
        redirect.addFlashAttribute("successMessage", "Product saved successfully.");
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{upc}")
    public String deleteProduct(@PathVariable String upc,
                                RedirectAttributes redirect) {
		redirect.addFlashAttribute("copyrightName", getGeneralDataString("copyrightName"));
		redirect.addFlashAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		redirect.addFlashAttribute("mainStyle", getGeneralDataString("mainStyle"));
        productService.delete(upc);
        redirect.addFlashAttribute("successMessage", "Product deleted.");
        return "redirect:/admin/products";
    }
}
