package com.littlestore.entity;

import java.io.Serializable;
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

	public enum States {NJ, PA}
	public enum PaymentMethods {CASH, CASHAPP, VENMO, PAYPAL, ZELLE, OTHER}

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
	private States state;									// 'NJ','PA'
	
	@Basic
	@Column(name="preferredPayment", nullable=true)
	private PaymentMethods preferredPayment;				// 'Cash','Cash App','Venmo','Paypal','Zelle','Other'

	@Basic
	@Column(name="paymentHandle", length=50, nullable=true)
	private String paymentHandle;							// Name/ID on Preferred Payment Platform

	@Basic
	@Column(name="isEnabled", nullable=false, columnDefinition="boolean default true")	// Set to false if user account is deleted or disabled by admin
	private Boolean isEnabled;
	
 /*   @ManyToMany 
    @JoinTable(name = "customer_role",
    	joinColumns = @JoinColumn(name = "customerId", referencedColumnName = "id"), 
    	inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"))*/
    @ManyToMany
    private Set<Role> role;


    public Customer() {
	}

	public Customer(int id, String email, String password, String firstName, String lastName, String phone,
			String address, String city, States state, PaymentMethods preferredPayment, String paymentHandle,
			Set<Role> role, Boolean isEnabled) {
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
		this.role = role;
		this.isEnabled = isEnabled;
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

	public Set<Role> getRole() {
		return role;
	}
	public void setRole(Set<Role> role) {
		this.role = (HashSet<Role>) role;
	}

	public Boolean getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
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
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + id;
		result = prime * result + ((isEnabled == null) ? 0 : isEnabled.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((passwordConfirm == null) ? 0 : passwordConfirm.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
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
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (passwordConfirm == null) {
			if (other.passwordConfirm != null)
				return false;
		} else if (!passwordConfirm.equals(other.passwordConfirm))
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
		if (preferredPayment == null) {
			if (other.preferredPayment != null)
				return false;
		} else if (!preferredPayment.equals(other.preferredPayment))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} /*else {
			for (Role r : role) {
				if (!(other.role..contains(r)))
					return false;
			}
		}*/
		if (state != other.state)
			return false;
		return true;
	}
}
