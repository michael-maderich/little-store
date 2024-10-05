package com.littlestore.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="product")
public class Product implements Serializable {

	private static final long serialVersionUID = 1234567891L;

	@Id
	@Column(name="upc", length=12, nullable=false, unique=true)
	private String upc;

	@Basic(optional=false)
	@Column(name="categoryMain", length=50, nullable=false)
	private String categoryMain;

	@Basic(optional=false)
	@Column(name="categorySpecific", length=50, nullable=false)
	private String categorySpecific;

	@Basic(optional=false)
	@Column(name="name", length=50, nullable=false)
	private String name;
	
	@Basic
	@Column(name="options", length=50)
	private String options;

	@Basic
	@Column(name="size", length=10)
	private String size;

	@Basic
	@Column(name="cost", nullable=false)
	private float cost;

	@Basic
	@Column(name="basePrice", nullable=false)
	private float basePrice;

	@Basic
	@Column(name="currentPrice", nullable=false)
	private float currentPrice;

	@Basic
	@Column(name="onSale", nullable=false)
	private boolean onSale;

	@Basic
	@Column(name="stockQty", nullable=false)
	private int stockQty;

	@Basic
	@Column(name="purchaseLimit", nullable=false)
	private int purchaseLimit;

	@Basic
	@Column(name="description", length=200)
	private String description;

	@Basic
	@Column(name="image", length=255)
	private String image;	// URL of product image

	@Basic
	@Column(name="transparent", nullable=false)
	private int transparent;	// whether image has transparent background

	@Basic
	@Column(name="dateAdded", nullable=false)
	private LocalDateTime dateAdded;
	
	@Basic
	@Column(name="dateLastSold", nullable=true)
	private LocalDateTime dateLastSold;
	
	/*	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="creationDate", nullable=false)
	private Date createDate*/
	
	public Product() {
	}

	public Product(String upc, String categoryMain, String categorySpecific, String name, String options, String size,
			float cost, float basePrice, float currentPrice, boolean onSale, int stockQty, int purchaseLimit,
			String description, String image, int transparent, LocalDateTime dateAdded, LocalDateTime dateLastSold) {
		this.upc = upc;
		this.categoryMain = categoryMain;
		this.categorySpecific = categorySpecific;
		this.name = name;
		this.options = options;
		this.size = size;
		this.cost = cost;
		this.basePrice = basePrice;
		this.currentPrice = currentPrice;
		this.onSale = onSale;
		this.stockQty = stockQty;
		this.purchaseLimit = purchaseLimit;
		this.description = description;
		this.image = image;
		this.transparent = transparent;
		this.dateAdded = dateAdded;
		this.dateLastSold = dateLastSold;
	}

	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	
	public String getCategoryMain() {
		return categoryMain;
	}
	public void setCategoryMain(String categoryMain) {
		this.categoryMain = categoryMain;
	}

	public String getCategorySpecific() {
		return categorySpecific;
	}
	public void setCategorySpecific(String categorySpecific) {
		this.categorySpecific = categorySpecific;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}

	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}

	public float getCost() {
		return cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(float basePrice) {
		this.basePrice = basePrice;
	}

	public float getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(float currentPrice) {
		this.currentPrice = currentPrice;
	}

	public boolean isOnSale() {
		return onSale;
	}
	public void setOnSale(boolean onSale) {
		this.onSale = onSale;
	}

	public int getStockQty() {
		return stockQty;
	}
	public void setStockQty(int stockQty) {
		this.stockQty = stockQty;
	}

	public int getPurchaseLimit() {
		return purchaseLimit;
	}
	public void setPurchaseLimit(int purchaseLimit) {
		this.purchaseLimit = purchaseLimit;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public int getTransparent() {
		return transparent;
	}
	public void setTransparent(int isTransparent) {
		this.transparent = isTransparent;
	}

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public LocalDateTime getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(LocalDateTime dateAdded) {
		this.dateAdded = dateAdded;
	}

	public LocalDateTime getDateLastSold() {
		return dateLastSold;
	}
	public void setDateLastSold(LocalDateTime dateLastSold) {
		this.dateLastSold = dateLastSold;
	}

	@Override
	public String toString() {
		return "Product [upc=" + upc + ", " + name + " " + size + " " + options + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(basePrice);
		result = prime * result + ((categoryMain == null) ? 0 : categoryMain.hashCode());
		result = prime * result + ((categorySpecific == null) ? 0 : categorySpecific.hashCode());
		result = prime * result + Float.floatToIntBits(cost);
		result = prime * result + Float.floatToIntBits(currentPrice);
		result = prime * result + ((dateAdded == null) ? 0 : dateAdded.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (onSale ? 1231 : 1237);
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + purchaseLimit;
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + stockQty;
		result = prime * result + ((upc == null) ? 0 : upc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (Float.floatToIntBits(basePrice) != Float.floatToIntBits(other.basePrice))
			return false;
		if (categoryMain == null) {
			if (other.categoryMain != null)
				return false;
		} else if (!categoryMain.equals(other.categoryMain))
			return false;
		if (categorySpecific == null) {
			if (other.categorySpecific != null)
				return false;
		} else if (!categorySpecific.equals(other.categorySpecific))
			return false;
		if (Float.floatToIntBits(cost) != Float.floatToIntBits(other.cost))
			return false;
		if (Float.floatToIntBits(currentPrice) != Float.floatToIntBits(other.currentPrice))
			return false;
		if (dateAdded == null) {
			if (other.dateAdded != null)
				return false;
		} else if (!dateAdded.equals(other.dateAdded))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (onSale != other.onSale)
			return false;
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (!options.equals(other.options))
			return false;
		if (purchaseLimit != other.purchaseLimit)
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (stockQty != other.stockQty)
			return false;
		if (upc == null) {
			if (other.upc != null)
				return false;
		} else if (!upc.equals(other.upc))
			return false;
		return true;
	}
}
