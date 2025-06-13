package com.littlestore.controller.admin;

import com.littlestore.config.GmailProperties;
import com.littlestore.controller.BaseController;
import com.littlestore.entity.Product;
import com.littlestore.service.ProductService;
import com.littlestore.utils.Utils;
import com.littlestore.service.GmailEmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;

import javax.imageio.ImageIO;
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

    // Helper to populate category dropdowns in both create & edit forms
    private void populateCategoryDropdowns(Model model) {
        List<String> allCategoryMain      = productService.listCategoryMain();
        List<String> allCategorySpecific  = productService.listCategorySpecific();

        model.addAttribute("allCategoryMain", allCategoryMain);
        model.addAttribute("allCategorySecondary", allCategoryMain);
        model.addAttribute("allCategorySpecific", allCategorySpecific);
    }

    // Helper to provide field max lengths for Product new/create
    private void populateProductFieldLengths(Model model) {
    	model.addAttribute("maxLenUpc", Utils.getColumnLength(Product.class, "upc", 12));
    	model.addAttribute("maxLenCategoryMain", Utils.getColumnLength(Product.class, "categoryMain", 50));
    	model.addAttribute("maxLenCategorySecondary", Utils.getColumnLength(Product.class, "categorySecondary", 50));
    	model.addAttribute("maxLenCategorySpecific", Utils.getColumnLength(Product.class, "categorySpecific", 50));
    	model.addAttribute("maxLenName", Utils.getColumnLength(Product.class, "name", 50));
    	model.addAttribute("maxLenOptions", Utils.getColumnLength(Product.class, "options", 75));
    	model.addAttribute("maxLenSize", Utils.getColumnLength(Product.class, "size", 20));
//    	model.addAttribute("maxLenDescription", Utils.getColumnLength(Product.class, "description", 255));
//    	model.addAttribute("maxLenImage", Utils.getColumnLength(Product.class, "image", 255));
    }

    @GetMapping
    public String listProducts(Model model, @ModelAttribute("successMessage") String successMessage) {
        List<Product> products = productService.listAll();
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
        model.addAttribute("products", products);
        // If there was a flash successMessage, the model now contains it.
        // The JSP will render it if not empty.
        if (successMessage != null && !successMessage.isEmpty()) {
            model.addAttribute("successMessage", successMessage);
        }
        return "admin/products/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		populateCategoryDropdowns(model);
        model.addAttribute("product", new Product());
        model.addAttribute("isNew", true);
        populateProductFieldLengths(model);
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
            model.addAttribute("errorMessage", "No such product with UPC: " + upc);
            return "admin/products/list";
//            return "redirect:/admin/products";
        }

		populateCategoryDropdowns(model);
        populateProductFieldLengths(model);
        model.addAttribute("product", product);
        model.addAttribute("isNew", false);
        return "admin/products/form";
    }

    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute Product product,
    						  BindingResult bindingResult,
    						  @RequestParam(value="isNew", required=false, defaultValue="true") boolean isNew,
    				          @RequestParam(value = "newCategoryMain", required = false) String newCategoryMain,
    				          @RequestParam(value = "newCategorySpecific", required = false) String newCategorySpecific,
            				  @RequestParam("imageFile") MultipartFile imageFile,
                              RedirectAttributes redirect,
                              Model model) {
		model.addAttribute("copyrightName", getGeneralDataString("copyrightName"));
		model.addAttribute("copyrightUrl", getGeneralDataString("copyrightUrl"));
		model.addAttribute("mainStyle", getGeneralDataString("mainStyle"));
		  System.out.println(">> saveProduct called: upc=" 
			      + product.getUpc() + ", isNew=" + isNew + ", transparent=" + product.getTransparent());

        // ─────────────────────────────────────────────────────────────
        // A) Check required fields via Bean Validation
        // ─────────────────────────────────────────────────────────────
        if (bindingResult.hasErrors()) {
            // **** Debug: print all field errors to the console/log ****
            for (FieldError fe : bindingResult.getFieldErrors()) {
                System.out.println("Validation error on field: "
                    + fe.getField() + " → " + fe.getDefaultMessage());
            }
            // Re-inject dropdown lists, so form.jsp can re-render them
    		populateCategoryDropdowns(model);
            model.addAttribute("errorMessage", "Please fix the errors below and resubmit.");
            return "admin/products/form";
        }

        // ─────────────────────────────────────────────────────────────
        // B) UPC uniqueness check (only for create)
        // ─────────────────────────────────────────────────────────────
        if (isNew && productService.get(product.getUpc()) != null) {
            bindingResult.rejectValue("upc", "error.product", "A product with that UPC already exists.");
    		populateCategoryDropdowns(model);
            model.addAttribute("errorMessage", "Please fix the errors below and resubmit.");
            return "admin/products/form";
        }

        // ─────────────────────────────────────────────────────────────
        // C) Handle “New...” logic for categoryMain and categorySpecific
        // ─────────────────────────────────────────────────────────────
        if ("__OTHER__".equals(product.getCategoryMain())) {
            if (newCategoryMain == null || newCategoryMain.trim().isEmpty()) {
                bindingResult.rejectValue("categoryMain", "error.product", "Please enter a new Main Category or select one");
                populateCategoryDropdowns(model);
                model.addAttribute("errorMessage", "Please fix the errors below and resubmit.");
                return "admin/products/form";
            }
            else if (newCategoryMain.trim().length() > 50) {
                bindingResult.rejectValue("categoryMain", "error.product", "Main Category name may not exceed 50 characters");
                populateCategoryDropdowns(model);
                model.addAttribute("errorMessage", "Please fix the errors below and resubmit.");
                return "admin/products/form";
            }
            else {
            	product.setCategoryMain(newCategoryMain.trim());
            }
        }

        if ("__OTHER__".equals(product.getCategorySpecific())) {
            if (newCategorySpecific == null || newCategorySpecific.trim().isEmpty()) {
                bindingResult.rejectValue("categorySpecific", "error.product", "Please enter a new Sub-Category or select one");
                populateCategoryDropdowns(model);
                model.addAttribute("errorMessage", "Please fix the errors below and resubmit.");
                return "admin/products/form";
            }
            else if (newCategorySpecific.trim().length() > 50) {
                bindingResult.rejectValue("categorySpecific", "error.product", "Sub-Category name may not exceed 50 characters");
                populateCategoryDropdowns(model);
                model.addAttribute("errorMessage", "Please fix the errors below and resubmit.");
                return "admin/products/form";
            }
            else {
            	product.setCategorySpecific(newCategorySpecific.trim());
            }
        }

        // ─────────────────────────────────────────────────────────────
        // D) Handle image upload if a new file was provided
        // ─────────────────────────────────────────────────────────────

        // Require an image only on Create
        if (isNew && (imageFile == null || imageFile.isEmpty())) {
            // If creating and no image was uploaded, force an error
            bindingResult.rejectValue("image", "error.product", "Image is required");
            populateCategoryDropdowns(model);
            model.addAttribute("errorMessage", "Please fix the errors below and resubmit.");
            return "admin/products/form";
        }
        
        // Handle upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
            	// If editing a Product and an image was provided, delete the old image
            	if (!isNew) {
	                String oldFolder  = productService.get(product.getUpc()).getCategoryMain().replaceAll("\\s+","_")
	                        + "/" +
	                        productService.get(product.getUpc()).getCategorySpecific().replaceAll("\\s+","_");
	            	String oldPublicId = oldFolder + "/" + product.getUpc();
	
	            	// b) Delete the old image
				    /*Map<?,?> destroyResult =*/ cloudinary.uploader().destroy(
				        oldPublicId,
				        ObjectUtils.asMap(
				          "invalidate", true      // also purge CDN caches
				        )
				    );
				    // (You can inspect destroyResult.get("result") for “ok” or “not found”)
            	}

			    String safeMain     = product.getCategoryMain().replaceAll("\\s+", "_");
            	String safeSpecific = product.getCategorySpecific().replaceAll("\\s+", "_");
            	String folderPath = safeMain + "/" + safeSpecific;

            	byte[] data;
            	try (InputStream in = imageFile.getInputStream()) {
            	  data = in.readAllBytes();   // Java 11+, or use ByteArrayOutputStream in older Java
            	}

            	Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    data,
                    ObjectUtils.asMap(
                        "resource_type", "image",
                        "folder", folderPath,
                        "public_id", product.getUpc(),
                        "colors", true      	// request color data
                    )
                );

                product.setImage((String) uploadResult.get("secure_url"));

                // Check for image transparency
                Boolean hasAlpha = false;

                try (ByteArrayInputStream bin = new ByteArrayInputStream(data)) {
                    BufferedImage img = ImageIO.read(bin);
                    if (img != null && img.getColorModel().hasAlpha()) {
                        hasAlpha = true;
                    }
                }

                @SuppressWarnings("unchecked")
                List<List<Object>> colors =
                (List<List<Object>>)uploadResult.get("colors");

                // Any color entry with an 8-digit hex (RRGGBBAA) → transparency
                if (null != colors
            		&& colors.stream()
                		  .map(c -> (String)c.get(0))
                		  .anyMatch(hex -> hex.length() == 9))
                	hasAlpha = true;

                product.setTransparent(hasAlpha);

            } catch (IOException ioex) {
                bindingResult.rejectValue("imageUrl", "error.product", "Image upload failed");
                populateCategoryDropdowns(model);
                model.addAttribute("errorMessage", "Please fix the errors below and resubmit.");
                return "admin/products/form";
            }
        }
        // If editing and they didn’t choose a new file, we leave product.getImageUrl() as-is.

        // ─────────────────────────────────────────────────────────────
        // E) Build the computed fields
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
        // F) Persist and redirect
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
