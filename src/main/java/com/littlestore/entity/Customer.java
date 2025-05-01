package com.littlestore.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="customer")
public class Customer implements Serializable {

	private static final long serialVersionUID = 12546552190L;

	public enum States {DE, NJ, PA}
	public enum PaymentMethods {CASH, CASHAPP, VENMO, PAYPAL, ZELLE, CHIME, OTHER}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", length=10, nullable=false, unique=true)
	private int id;
	
	@Basic(optional=false)
	@Column(name="email", length=50, nullable=false, unique=true)
	private String email;
	
	@Basic(optional=false)
	@Column(name="password", length=60, nullable=false)
	private String password;
	
    @Transient
    private String passwordConfirm;

	@Basic(optional=false)
	@Column(name="firstName", length=50, nullable=false)
	private String firstName;

	@Basic(optional=false)
	@Column(name="lastName", length=50, nullable=false)
	private String lastName;

	@Basic
	@Column(name="phone", length=10, nullable=true)
	private String phone;

	@Basic
	@Column(name="address", length=50, nullable=true)
	private String address;

	@Basic
	@Column(name="city", length=50, nullable=true)
	private String city;

	@Basic
	@Column(name="state", nullable=true)
	private States state;									// 'DE','NJ','PA'
	
	@Basic
	@Column(name="preferredPayment", nullable=true)
	private PaymentMethods preferredPayment;				// 'Cash','Cash App','Venmo','Paypal','Zelle','Other'

	@Basic
	@Column(name="paymentHandle", length=50, nullable=true)
	private String paymentHandle;							// Name/ID on Preferred Payment Platform

	@Basic
	@Column(name="isEnabled", nullable=false, columnDefinition="boolean default true")	// Set to false if user account is deleted or disabled by admin
	private Boolean isEnabled;
	
	@Basic
	@Column(name="lastVisited", nullable=true)
	private LocalDateTime lastVisited;
	
	@Basic
	@Column(name="lastOrdered", nullable=true)
	private LocalDateTime lastOrdered;
	
	@Basic
	@Column(name="accountCreated", nullable=false)
	private LocalDateTime accountCreated;
	
	@Basic
	@Column(name="emailSub", nullable=false, columnDefinition="boolean default true")	// Set to false if user account is deleted or disabled by admin
	private Boolean emailSub;
	
	@Basic
	@Column(name="reset_token", length=255, nullable=true)
	private String resetToken;

	@Basic
	@Column(name="reset_token_expiry", nullable=true)
	private LocalDateTime resetTokenExpiry;

 /*   @ManyToMany 
    @JoinTable(name = "customer_role",
    	joinColumns = @JoinColumn(name = "customerId", referencedColumnName = "id"), 
    	inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"))*/
    @ManyToMany
    private Set<Role> role;


    public Customer() {
	}

	public Customer(int id, String email, String password, String passwordConfirm, String firstName, String lastName,
			String phone, String address, String city, States state, PaymentMethods preferredPayment, String paymentHandle,
			Boolean isEnabled, LocalDateTime lastVisited, LocalDateTime lastOrdered, LocalDateTime accountCreated,
			Boolean emailSub, String resetToken, LocalDateTime resetTokenExpiry, Set<Role> role) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.address = address;
		this.city = city;
		this.state = state;
		this.preferredPayment = preferredPayment;
		this.paymentHandle = paymentHandle;
		this.isEnabled = isEnabled;
		this.lastVisited = lastVisited;
		this.lastOrdered = lastOrdered;
		this.accountCreated = accountCreated;
		this.emailSub = emailSub;
		this.resetToken = resetToken;
		this.resetTokenExpiry = resetTokenExpiry;
		this.role = role;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public States getState() {
		return state;
	}
	public void setState(States state) {
		this.state = state;
	}

	public PaymentMethods getPreferredPayment() {
		return preferredPayment;
	}
	public void setPreferredPayment(PaymentMethods preferredPayment) {
		this.preferredPayment = preferredPayment;
	}

	public String getPaymentHandle() {
		return paymentHandle;
	}
	public void setPaymentHandle(String paymentHandle) {
		this.paymentHandle = paymentHandle;
	}

	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	
	public LocalDateTime getLastVisited() {
		return lastVisited;
	}
	public void setLastVisited(LocalDateTime lastVisited) {
		this.lastVisited = lastVisited;
	}

	public LocalDateTime getLastOrdered() {
		return lastOrdered;
	}
	public void setLastOrdered(LocalDateTime lastOrdered) {
		this.lastOrdered = lastOrdered;
	}

	public LocalDateTime getAccountCreated() {
		return accountCreated;
	}
	public void setAccountCreated(LocalDateTime accountCreated) {
		this.accountCreated = accountCreated;
	}

	public Boolean getEmailSub() {
		return emailSub;
	}
	public void setEmailSub(Boolean emailSub) {
		this.emailSub = emailSub;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public LocalDateTime getResetTokenExpiry() {
		return resetTokenExpiry;
	}

	public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
		this.resetTokenExpiry = resetTokenExpiry;
	}

	public Set<Role> getRole() {
		return role;
	}
	public void setRole(Set<Role> role) {
		this.role = (HashSet<Role>) role;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", email=" + email + ", password=" + password + ", passwordConfirm="
				+ passwordConfirm + ", firstName=" + firstName + ", lastName=" + lastName + ", phone=" + phone
				+ ", address=" + address + ", city=" + city + ", state=" + state + ", preferredPayment="
				+ preferredPayment + ", paymentHandle=" + paymentHandle + ", isEnabled=" + isEnabled + ", role=" + role
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountCreated == null) ? 0 : accountCreated.hashCode());
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((emailSub == null) ? 0 : emailSub.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + id;
		result = prime * result + ((isEnabled == null) ? 0 : isEnabled.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((lastVisited == null) ? 0 : lastVisited.hashCode());
		result = prime * result + ((lastOrdered == null) ? 0 : lastOrdered.hashCode());
		result = prime * result + ((paymentHandle == null) ? 0 : paymentHandle.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((preferredPayment == null) ? 0 : preferredPayment.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Customer))
			return false;
		Customer other = (Customer) obj;
		if (accountCreated == null) {
			if (other.accountCreated != null)
				return false;
		} else if (!accountCreated.equals(other.accountCreated))
			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (emailSub == null) {
			if (other.emailSub != null)
				return false;
		} else if (!emailSub.equals(other.emailSub))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (isEnabled == null) {
			if (other.isEnabled != null)
				return false;
		} else if (!isEnabled.equals(other.isEnabled))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (lastVisited == null) {
			if (other.lastVisited != null)
				return false;
		} else if (!lastVisited.equals(other.lastVisited))
			return false;
		if (lastOrdered == null) {
			if (other.lastOrdered != null)
				return false;
		} else if (!lastOrdered.equals(other.lastOrdered))
			return false;
		if (paymentHandle == null) {
			if (other.paymentHandle != null)
				return false;
		} else if (!paymentHandle.equals(other.paymentHandle))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (preferredPayment != other.preferredPayment)
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (state != other.state)
			return false;
		return true;
	}
}
