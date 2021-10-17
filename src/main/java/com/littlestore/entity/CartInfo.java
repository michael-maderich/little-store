package com.littlestore.entity;

import java.util.ArrayList;
import java.util.List;
 
public class CartInfo {
 
    private int orderNum;
 
    private Customer customer;
 
    private final List<CartLineInfo> cartLines = new ArrayList<CartLineInfo>();
 
    public CartInfo() {
 
    }
 
    public int getOrderNum() {
        return orderNum;
    }
 
    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }
 
    public Customer getCustomer() {
        return customer;
    }
 
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
 
    public List<CartLineInfo> getCartLines() {
        return this.cartLines;
    }
 
    private CartLineInfo findLineByUpc(String upc) {
        for (CartLineInfo line : this.cartLines) {
            if (line.getProduct().getUpc().equals(upc)) {
                return line;
            }
        }
        return null;
    }
 
    public void addProduct(Product product, int quantity) {
        CartLineInfo line = this.findLineByUpc(product.getUpc());
 
        if (line == null) {
            line = new CartLineInfo();
            line.setQuantity(0);
            line.setProduct(product);
            this.cartLines.add(line);
        }
        int newQuantity = line.getQuantity() + quantity;
        if (newQuantity <= 0) {
            this.cartLines.remove(line);
        } else {
            line.setQuantity(newQuantity);
        }
    }
 
    public void validate() {
 
    }
 
    public void updateProduct(String upc, int quantity) {
        CartLineInfo line = this.findLineByUpc(upc);
 
        if (line != null) {
            if (quantity <= 0) {
                this.cartLines.remove(line);
            } else {
                line.setQuantity(quantity);
            }
        }
    }
 
    public void removeProduct(Product product) {
        CartLineInfo line = this.findLineByUpc(product.getUpc());
        if (line != null) {
            this.cartLines.remove(line);
        }
    }
 
    public boolean isEmpty() {
        return this.cartLines.isEmpty();
    }
 
    public boolean isValidCustomer() {
        return this.customer != null;
    }
 
    public int getQuantityTotal() {
        int quantity = 0;
        for (CartLineInfo line : this.cartLines) {
            quantity += line.getQuantity();
        }
        return quantity;
    }
 
    public double getAmountTotal() {
        double total = 0;
        for (CartLineInfo line : this.cartLines) {
            total += line.getAmount();
        }
        return total;
    }
 
    public void updateQuantity(CartInfo cartForm) {
        if (cartForm != null) {
            List<CartLineInfo> lines = cartForm.getCartLines();
            for (CartLineInfo line : lines) {
                this.updateProduct(line.getProduct().getUpc(), line.getQuantity());
            }
        }
 
    }
 
}