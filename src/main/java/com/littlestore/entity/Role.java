package com.littlestore.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class Role {
	
	// Should we have a getter for this?
	public enum Roles {ADMIN, OWNER, CUSTOMER};		// Enumeration aligns with Role ID: 0=ADMIN, 1=OWNER, 2=CUSTOMER
	
    @Id
	@Column(name="id", length=11, nullable=false, unique=true)
    private int id;

	@Column(name="name", nullable=false)
    private String name;							// 'OWNER','ADMIN','CUSTOMER'

    @ManyToMany(mappedBy = "role")
    private Set<Customer> customer;

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

    public Set<Customer> getCustomer() {
        return customer;
    }
    public void setCustomer(Set<Customer> customer) {
        this.customer = customer;
    }
    
	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + id;
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
		Role other = (Role) obj;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} /*else if (!customer.equals(other.customer))
			return false;*/
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
