package com.littlestore.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="paymentInfo")
public class PaymentInfo implements Serializable {

	private static final long serialVersionUID = 1114567899L;

	@Id
	@Column(name="id", length=11, nullable=false, unique=true)
	private int id;

	@Basic(optional=false)
	@Column(name="name", length=50, nullable=false)
	private String name;

	@Basic(optional=false)
	@Column(name="handle", length=100, nullable=false)
	private String handle;
	
	@Basic(optional=true)
	@Column(name="linkHandle", length=100, nullable=true)
	private String linkHandle;
	
	@Basic(optional=true)
	@Column(name="link", length=255, nullable=true)
	private String link;
	
	@Basic(optional=true)
	@Column(name="defaultLink", length=255, nullable=true)
	private String defaultLink;
	
	public PaymentInfo() {
	}

	public PaymentInfo(int id, String name, String handle, String link, String defaultLink) {
		this.id = id;
		this.name = name;
		this.handle = handle;
		this.link = link;
		this.defaultLink = defaultLink;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getLinkHandle() {
		return linkHandle;
	}

	public void setLinkHandle(String linkHandle) {
		this.linkHandle = linkHandle;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDefaultLink() {
		return defaultLink;
	}

	public void setDefaultLink(String defaultLink) {
		this.defaultLink = defaultLink;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultLink == null) ? 0 : defaultLink.hashCode());
		result = prime * result + ((handle == null) ? 0 : handle.hashCode());
		result = prime * result + id;
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((linkHandle == null) ? 0 : linkHandle.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		PaymentInfo other = (PaymentInfo) obj;
		if (defaultLink == null) {
			if (other.defaultLink != null)
				return false;
		} else if (!defaultLink.equals(other.defaultLink))
			return false;
		if (handle == null) {
			if (other.handle != null)
				return false;
		} else if (!handle.equals(other.handle))
			return false;
		if (id != other.id)
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (linkHandle == null) {
			if (other.linkHandle != null)
				return false;
		} else if (!linkHandle.equals(other.linkHandle))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
