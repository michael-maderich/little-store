package com.littlestore.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.littlestore.entity.Customer;
import com.littlestore.entity.Role;
import com.littlestore.repository.CustomerRepository;
import com.littlestore.repository.RoleRepository;

@Service
//@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomerService(CustomerRepository customerRepository, RoleRepository roleRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void create(Customer customer) {	// Create new user DB entry (encrypt password and add roles)
		customer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
		Set<Role> userRoles = new HashSet<>();
		userRoles.add( roleRepository.findByName(Role.Roles.CUSTOMER.name()) );
		customer.setRole(userRoles);			// Customer role
        customerRepository.save(customer);
    }
	 
    public boolean emailCodeMatches(Customer customer, String emailHash) {
		return bCryptPasswordEncoder.matches(customer.getEmail(), emailHash);
    }
     
    @Transactional(rollbackFor = Exception.class)
    public void update(Customer customer) {	// Update user (don't re-encrypt password)
        customerRepository.save(customer);
    }
     
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(int custId, String newPassword) {
    	customerRepository.updatePassword(custId, newPassword);
    }

    public String encrypt(String word)
    {
    	return bCryptPasswordEncoder.encode(word);
    }
    
    public List<Customer> listAll() {
        return (List<Customer>) customerRepository.findAll();
    }
     
    public Customer findById(Integer id) {
        return customerRepository.findById(id).get();
    }

    @Transactional
    public void delete(Integer id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> findByName(String name) {
        return customerRepository.findByName(name);
    }
    
	public Customer findByEmail(String email) {
		return customerRepository.findByEmail(email);
	}

	public Customer findByResetToken(String token) {
		return customerRepository.findByResetToken(token);
	}
}